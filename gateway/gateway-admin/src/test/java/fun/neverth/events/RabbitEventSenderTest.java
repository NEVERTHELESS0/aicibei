package fun.neverth.events;

import fun.neverth.config.RabbitConfig;
import fun.neverth.entity.po.BasePO;
import fun.neverth.entity.vo.BaseVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * todo
 *
 * @author NeverTh
 * @date 2020/7/12 13:23
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitEventSenderTest {

    @Resource
    RabbitEventSender rabbitEventSender;

    @Test
    public void sendTest(){
        BasePO basePO = new BasePO();
        basePO.setId("123");
        basePO.setUpdatedBy("me");
        rabbitEventSender.send(RabbitConfig.ROUTING_KEY, basePO);
    }
}