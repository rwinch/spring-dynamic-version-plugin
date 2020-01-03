/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.spring.gradle.version;

import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.api.Project;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A simple unit test for the 'io.spring.gradle.version.greeting' plugin.
 */
public class SpringDynamicVersionPluginPluginTest {
    @Test public void pluginRegistersATask() {
        // Create a test project and apply the plugin
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("io.spring.gradle.version.greeting");

        // Verify the result
        assertNotNull(project.getTasks().findByName("greeting"));
    }
}
