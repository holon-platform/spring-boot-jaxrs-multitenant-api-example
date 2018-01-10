package com.holonplatform.example;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.annotation.RequestScope;

import com.holonplatform.auth.jwt.JwtConfiguration;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.tenancy.TenantResolver;
import com.holonplatform.http.servlet.ServletHttpRequest;
import com.holonplatform.spring.ScopeTenant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@SpringBootApplication
public class Application {

	public static final String TENANT_ID_JWT_CLAIM = "tenantId";
	
	@Bean
	@RequestScope
	public TenantResolver tenantResolver(HttpServletRequest request, JwtConfiguration configuration) {
		// use Holon ServletHttpRequest to easily obtain the Authorization header Bearer value
		return () -> ServletHttpRequest.create(request).getAuthorizationBearer().map(bearer -> {
			// parse JWT and get claims
			Claims claims = Jwts.parser().setSigningKey(configuration.getSharedKey()).parseClaimsJws(bearer).getBody();
			// return the tenantId claim value
			return claims.get(TENANT_ID_JWT_CLAIM, String.class);
		});
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
