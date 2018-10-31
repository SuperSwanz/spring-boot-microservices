package com.greyseal.microservice.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentNotFoundException;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(method = RequestMethod.GET, path = "/loader")
public class ConfigController {
	@Autowired
	private EnvironmentRepository repository;
	
	private static final String ENVIRONMENT = "environments";

	private boolean acceptEmpty = true;

	@RequestMapping("/{name}/{profiles:.*[^-].*}")
	public ConfigurationBuilder getProperties(@PathVariable String name, @PathVariable String profiles) {
		final Environment environment = labelled(name, profiles, null);
		Map<?, ?> propertyValueMap = null;
		String propertySourceName = null;
		for (PropertySource propertySource : environment.getPropertySources()) {
			final String profile = String.join(".", ENVIRONMENT, profiles, " ").trim();
			propertyValueMap = getMap(propertySource,  profile);
			propertySourceName = propertySource.getName();
		}
		final PropertySource source = new PropertySource(propertySourceName, propertyValueMap);
		final ConfigurationBuilder builder = new ConfigurationBuilder.Builder(environment.getName())
				.setLabel(environment.getLabel()).setState(environment.getState()).setVersion(environment.getVersion())
				.setProfiles(environment.getProfiles()).build();
		builder.getPropertySources().add(source);
		return builder;
	}

	@RequestMapping("/{name}/{profiles}/{label:.*}")
	public Environment labelled(@PathVariable String name, @PathVariable String profiles, @PathVariable String label) {
		if (name != null && name.contains("(_)")) {
			// "(_)" is uncommon in a git repo name, but "/" cannot be matched
			// by Spring MVC
			name = name.replace("(_)", "/");
		}
		if (label != null && label.contains("(_)")) {
			// "(_)" is uncommon in a git branch name, but "/" cannot be matched
			// by Spring MVC
			label = label.replace("(_)", "/");
		}
		final Environment environment = this.repository.findOne(name, profiles, label);
		if (!acceptEmpty && (environment == null || environment.getPropertySources().isEmpty())) {
			throw new EnvironmentNotFoundException("Profile Not found");
		}
		return environment;
	}

	private Map<?, ?> getMap(final PropertySource source, final String environment) {
		final Map<Object, Object> map = new LinkedHashMap<>();
		final Map<?, ?> input = (Map<?, ?>) source.getSource();
		for (Object key : input.keySet()) {
			final String _key = (String) key;
			final String _env = _key.substring(0, environment.length());
			final String _value = _key.substring(environment.length());
			if (_env.equals(environment))
				map.put(_value, input.get(key.toString()));
		}
		return map;
	}
}