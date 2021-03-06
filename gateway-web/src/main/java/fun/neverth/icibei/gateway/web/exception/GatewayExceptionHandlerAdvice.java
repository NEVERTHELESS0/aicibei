package fun.neverth.icibei.gateway.web.exception;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netflix.ribbon.proxy.annotation.Http;
import feign.FeignException;
import fun.neverth.icibei.common.core.exception.SystemErrorType;
import fun.neverth.icibei.common.core.vo.Result;
import io.netty.channel.ConnectTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.Optional;

/**
 * 异常处理器，包含鉴权失败处理，将鉴权失败转为json形式
 *
 * @author NeverTh
 * @date 14:56 2020/10/6
 */
@Slf4j
@Component
public class GatewayExceptionHandlerAdvice {
    @ExceptionHandler(value = {ResponseStatusException.class})
    public Result handle(ResponseStatusException ex) {
        log.warn("response status exception:{}", ex.getMessage());
        return Result.fail(SystemErrorType.GATEWAY_ERROR, ex.getMessage());
    }

    @ExceptionHandler(value = {ConnectTimeoutException.class})
    public Result handle(ConnectTimeoutException ex) {
        log.warn("connect timeout exception:{}", ex.getMessage());
        return Result.fail(SystemErrorType.GATEWAY_CONNECT_TIME_OUT, ex.getMessage());
    }

    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result handle(NotFoundException ex) {
        log.warn("not found exception:{}", ex.getMessage());
        return Result.fail(SystemErrorType.GATEWAY_NOT_FOUND_SERVICE, ex.getMessage());
    }

    @ExceptionHandler(value = {SignatureException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result handle(SignatureException ex) {
        log.warn("SignatureException:{}", ex.getMessage());
        return Result.fail(SystemErrorType.INVALID_TOKEN, ex.getMessage());
    }

    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handle(RuntimeException ex) {
        log.warn("runtime exception:{}", ex.getMessage());
        return Result.fail(ex.getMessage());
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handle(Exception ex) {
        log.warn("exception:{}", ex.getMessage());
        return Result.fail(ex.getMessage());
    }

    @ExceptionHandler(value = {FeignException.class})
    public Result<JSONObject> handle(FeignException ex) {
        JSONObject jsonObject = new JSONObject();

        Optional<ByteBuffer> byteBuffer = ex.responseBody();
        jsonObject.put("detailMessage", ex.getMessage());
        byteBuffer.ifPresent(e -> jsonObject.putAll(JSON.parseObject(StandardCharsets.UTF_8.decode(e).toString())));

        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status != HttpStatus.INTERNAL_SERVER_ERROR) {
            return Result.success(jsonObject);
        } else {
            return Result.fail(jsonObject);
        }
    }

    @ExceptionHandler(value = {Throwable.class})
    public Result handle(Throwable throwable) {
        Result result;
        if (throwable instanceof ResponseStatusException) {
            result = handle((ResponseStatusException) throwable);
        } else if (throwable instanceof ConnectTimeoutException) {
            result = handle((ConnectTimeoutException) throwable);
        } else if (throwable instanceof FeignException) {
            result = handle((FeignException) throwable);
        } else if (throwable instanceof RuntimeException) {
            result = handle((RuntimeException) throwable);
        } else {
            result = Result.fail(SystemErrorType.GATEWAY_EXCEPTION_HANDLER_ERROR, throwable.getMessage());
        }
        return result;
    }
}
