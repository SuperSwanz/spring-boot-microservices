package com.greyseal.microservice.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.config.environment.PropertySource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = ConfigurationBuilder.Builder.class)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ConfigurationBuilder {
	private final String name;
	private String[] profiles;
	private String label;
	private List<PropertySource> propertySources;
	private String version;
	private String state;

	private ConfigurationBuilder(Builder builder) {
		this.name = builder.name;
		this.profiles = builder.profiles;
		this.label = builder.label;
		this.propertySources = builder.propertySources;
		this.version = builder.version;
		this.state = builder.state;
	}

	@JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Builder {
		// Setting Required parameters
		private final String name;

		// Setting Optional parameters
		private String[] profiles = new String[0];
		private String label;
		private List<PropertySource> propertySources = new ArrayList<>();
		private String version;
		private String state;

		public Builder(@JsonProperty("name") String name) {
			this.name = name;
		}

		public Builder setProfiles(@JsonProperty String[] profiles) {
			this.profiles = profiles;
			return this;
		}

		public Builder setLabel(@JsonProperty String label) {
			this.label = label;
			return this;
		}

		public Builder setPropertySources(@JsonProperty List<PropertySource> propertySources) {
			this.propertySources = propertySources;
			return this;
		}

		public Builder setVersion(@JsonProperty String version) {
			this.version = version;
			return this;
		}

		public Builder setState(@JsonProperty String state) {
			this.state = state;
			return this;
		}

		public ConfigurationBuilder build() {
			return new ConfigurationBuilder(this);
		}
	}

	public String[] getProfiles() {
		return profiles;
	}

	public String getLabel() {
		return label;
	}

	public List<PropertySource> getPropertySources() {
		return propertySources;
	}

	public String getVersion() {
		return version;
	}

	public String getState() {
		return state;
	}

	public String getName() {
		return name;
	}
}