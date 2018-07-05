package com.mr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

/**
 * RestTemplate客户端配置
 */
@Configuration
@ConditionalOnClass(ProxyConfig.class)
public class RestTemplateConfig {

	@Value("${rest.readTimeout}")
	private int readTimeout;
	@Value("${rest.connectTimeout}")
	private int connectionTimeout;
	@Autowired
	private ProxyConfig proxyConfig;

	@Bean
	public SimpleClientHttpRequestFactory httpClientFactory() {
		SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
		httpRequestFactory.setReadTimeout(readTimeout);
		httpRequestFactory.setConnectTimeout(connectionTimeout);

		if (proxyConfig.getEnabled()) {
			SocketAddress address = new InetSocketAddress(proxyConfig.getHost(), proxyConfig.getPort());
			Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
			httpRequestFactory.setProxy(proxy);
		}

		return httpRequestFactory;
	}

	/**
	 * 通过自动配置的RestTemplateBuilder创建自己需要的RestTemplate实例。自动配置的RestTemplateBuilder会确保应用到RestTemplate实例的HttpMessageConverters是合适的。
	 * RestTemplateBuilder包含很多有用的方法，可以用于快速配置一个RestTemplate, 默认使用的是SimpleClientHttpRequestFactory(jdk自带的HttpURLConnection)
	 * 例如，你可以使用builder.basicAuthorization("user", "password").build()添加基本的认证支持（BASIC auth）
	 *
	 * @return
	 */
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,
									 SimpleClientHttpRequestFactory httpClientFactory) {
		return restTemplateBuilder
				.setReadTimeout(30000) //ms
				.setConnectTimeout(15000) //ms
				.requestFactory(httpClientFactory)
				.build();
	}


	/****************************************spring传统 java config配置方式***************************************************/
	//    @Bean
//    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
//        RestTemplate restTemplate =  new RestTemplate();
//
//        //配置MessageConverter转换器
//        List<HttpMessageConverter<?>> httpMessageConverterList = new ArrayList<>();
//
//        httpMessageConverterList.add(new FormHttpMessageConverter());
//        httpMessageConverterList.add(new MappingJackson2XmlHttpMessageConverter());
//        httpMessageConverterList.add(new MappingJackson2HttpMessageConverter());
//        httpMessageConverterList.add(new StringHttpMessageConverter());
//
//        restTemplate.setMessageConverters(httpMessageConverterList);
//        restTemplate.setRequestFactory(factory);
//        return restTemplate;
//    }

//    @Bean
//    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        factory.setReadTimeout(5000);//ms
//        factory.setConnectTimeout(15000);//ms
//        return factory;
//    }
}