package fun.neverth.entity.form;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.neverth.entity.param.BaseParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * todo
 *
 * @author NeverTh
 * @date 2020/7/12 16:10
 */
@Slf4j
@Data
public class BaseQueryForm<P extends BaseParam> extends BaseForm{

    /**
     * 分页查询的参数，当前页数
     */
    private long current = 1;

    /**
     * 分页查询的参数，当前页面每页显示的数量
     */
    private long size = 10;

    /**
     * Form转化为Param
     */
    public P toParam(Class<P> clazz) {
        P p = BeanUtils.instantiateClass(clazz);
        BeanUtils.copyProperties(this, p);
        return p;
    }

    /**
     * 从form中获取page参数，用于分页查询参数
     */
    public Page getPage() {
        return new Page(this.getCurrent(), this.getSize());
    }
}