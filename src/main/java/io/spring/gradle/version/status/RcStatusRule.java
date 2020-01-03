package io.spring.gradle.version.status;

import org.gradle.api.artifacts.ComponentMetadataContext;
import org.gradle.api.artifacts.ComponentMetadataDetails;
import org.gradle.api.artifacts.ComponentMetadataRule;

import java.util.Arrays;

/**
 * @author Rob Winch
 */
public class RcStatusRule implements ComponentMetadataRule {
	@Override
	public void execute(ComponentMetadataContext context) {
		ComponentMetadataDetails details = context.getDetails();
		details.setStatusScheme(Arrays.asList("integration", "milestone", "rc", "release"));
		String version = context.getDetails().getId().getVersion();
		if (version.endsWith("-SNAPSHOT")) {
			details.setStatus("integration");
		} else if (version.matches("M\\d+")) {
			details.setStatus("milestone");
		} else if (version.matches("RC\\d+")) {
			details.setStatus("rc");
		} else {
			details.setStatus("release");
		}
	}
}
