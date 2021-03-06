package deployment;

import java.util.ArrayList;
import java.util.Locale;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class DeploymentPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        final NamedDomainObjectContainer<DeploymentProcessConfiguration> configs =
                project.container(DeploymentProcessConfiguration.class);
        configs.all(
                config -> {
                    config.environmentSteps = new ArrayList<>();
                });
        project.getExtensions().add("deployments", configs);

        project.afterEvaluate(this::createDeployTasks);
    }

    private void createDeployTasks(Project project) {
        final NamedDomainObjectContainer<DeploymentProcessConfiguration> configs =
                (NamedDomainObjectContainer<DeploymentProcessConfiguration>)
                        project.getExtensions().findByName("deployments");
        configs.all(
                config -> {
                    for (int stepIndex = 0;
                            stepIndex < config.environmentSteps.size();
                            stepIndex++) {
                        final String stepName = config.environmentSteps.get(stepIndex);
                        project.getTasks()
                                .register(
                                        String.format(
                                                Locale.ROOT,
                                                "deploymentRequest_%s_%s",
                                                config.name,
                                                stepName),
                                        DeploymentRequestProcessTask.class,
                                        config,
                                        stepIndex);
                    }
                });
    }
}
