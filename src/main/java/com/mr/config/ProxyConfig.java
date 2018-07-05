package com.mr.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by feng on 18-6-28
 */
@Component
@ConfigurationProperties(prefix = "proxy")
@Data
public class ProxyConfig {
	/**
	 * 是否启用代理
	 */
	private Boolean enabled;
	/**
	 * 代理主机地址
	 */
	private String host;
	/**
	 * 代理端口
	 */
	private Integer port;
}