package com.mr.modules.api;

import com.mr.framework.log.Log;
import com.mr.framework.log.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author
 * Created by JK on 2017/3/10.
 */
@RequestMapping("/api")
public class BaseController {

    protected final transient Log log = LogFactory.get(this.getClass());

    protected final transient String API_URL = "http://localhost/api";

}
