package com.holonplatform.example;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.annotation.RequestScope;

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.tenancy.TenantResolver;
import com.holonplatform.spring.ScopeTenant;

@SpringBootApplication
public class Application {

	@Bean
	@RequestScope
	public TenantResolver tenantResolver(HttpServletRequest request) {
		return () -> Optional.ofNullable(request.getHeader("X-TENANT-ID"));
	}

	@Bean
	@ScopeTenant
	public Datastore datastore(BeanFactory beanFactory, TenantResolver tenantResolver) {
		// get current tenant id
		String tenantId = tenantResolver.getTenantId()
				.orElseThrow(() -> new IllegalStateException("No tenant id available"));
		// get Datastore using tenantId qualifier
		return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, Datastore.class, tenantId);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
