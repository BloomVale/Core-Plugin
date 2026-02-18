package dev.casperrs.bloomVale.papermc;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.graph.Dependency;

public class BloomValeLoader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        // Example: future database or utility libraries
        resolver.addDependency(new Dependency(
                new DefaultArtifact("org.apache.commons:commons-lang3:3.14.0"),
                null
        ));

        resolver.addRepository(new RemoteRepository.Builder(
                "paper",
                "default",
                "https://repo.papermc.io/repository/maven-public/"
        ).build());

        classpathBuilder.addLibrary(resolver);
    }
}
