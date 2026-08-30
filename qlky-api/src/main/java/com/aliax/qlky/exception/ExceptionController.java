package com.aliax.qlky.exception;

import com.aliax.qlky.bean.basebean.HttpResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * @author 艾莉希雅
 */
@Log4j2
@ControllerAdvice
//异常处理器
public class ExceptionController implements ErrorController {
    // 捕获所有其他异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public HttpResult handleGenericException(Exception ex, Model model) {
        // 记录异常信息到控制台
        log.error("发生异常: {}", ex.getMessage(), ex);
        model.addAttribute("error", "Exception: " + ex.getMessage());
        if(ex instanceof RequestParamException){
            return HttpResult.fail(ex.getMessage());
        }
        return HttpResult.fail("系统异常，请稍后重试！");
    }
}
