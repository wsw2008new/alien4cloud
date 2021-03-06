package alien4cloud.tosca.repository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import javax.annotation.Resource;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.stereotype.Component;

import alien4cloud.component.ICSARRepositorySearchService;
import alien4cloud.exception.NotFoundException;
import alien4cloud.model.components.CSARDependency;
import alien4cloud.model.components.Csar;
import alien4cloud.model.components.IndexedToscaElement;
import alien4cloud.tosca.context.ToscaContext;
import alien4cloud.tosca.model.ArchiveRoot;
import alien4cloud.tosca.parser.ParsingContextExecution;
import alien4cloud.tosca.parser.ParsingResult;
import alien4cloud.tosca.parser.ToscaArchiveParser;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * Manages a file-based local archive repository.
 *
 * Note: You should always access this class through the ToscaContext.
 */
@Getter
@Setter
@Component
public class LocalRepositoryImpl implements ICSARRepositorySearchService {
    private static ThreadLocal<Boolean> recursiveCall = new ThreadLocal<>();
    @Resource
    private ToscaArchiveParser toscaArchiveParser;

    /** Path of the local repository. */
    private Path localRepositoryPath = Paths.get("target/repository");

    public void setPath(String path) {
        localRepositoryPath = Paths.get(path);
    }

    @Override
    public Csar getArchive(String id) {
        // name, version
        String[] split = id.split(":");
        CSARDependency dependency = new CSARDependency(split[0], split[1]);
        ArchiveRoot root = parse(dependency).getResult();
        return root == null ? null : root.getArchive();
    }

    @Override
    public boolean isElementExistInDependencies(Class<? extends IndexedToscaElement> elementClass, String elementId, Collection<CSARDependency> dependencies) {
        return getElementInDependencies(elementClass, elementId, dependencies) != null;
    }

    @Override
    public <T extends IndexedToscaElement> T getRequiredElementInDependencies(Class<T> elementClass, String elementId, Collection<CSARDependency> dependencies)
            throws NotFoundException {
        T element = getElementInDependencies(elementClass, elementId, dependencies);
        if (element == null) {
            throw new NotFoundException(
                    "Element elementId: <" + elementId + "> of type <" + elementClass.getSimpleName() + "> cannot be found in dependencies " + dependencies);
        }
        return element;
    }

    @Override
    public <T extends IndexedToscaElement> T getElementInDependencies(Class<T> elementClass, String elementId, Collection<CSARDependency> dependencies) {
        if (recursiveCall.get() == null) {
            recursiveCall.set(true);
        } else {
            return null;
        }
        // ensure that dependencies are loaded in the ToscaContext
        for (CSARDependency dependency : dependencies) {
            // parse and register the archive from local repository.
            parseAndRegister(dependency);
        }
        T element = ToscaContext.get(elementClass, elementId);
        recursiveCall.remove();
        return element;
    }

    @Override
    public <T extends IndexedToscaElement> T getElementInDependencies(Class<T> elementClass, QueryBuilder query, Collection<CSARDependency> dependencies) {
        return null;
    }

    @Override
    public <T extends IndexedToscaElement> T getParentOfElement(Class<T> elementClass, T indexedToscaElement, String parentElementId) {
        return null;
    }

    @SneakyThrows
    private void parseAndRegister(CSARDependency dependency) {
        // parse and load archive.
        ParsingResult<ArchiveRoot> result = parse(dependency);
        ToscaContext.Context context = ToscaContext.get();
        context.register(result.getResult());
    }

    @SneakyThrows
    private ParsingResult<ArchiveRoot> parse(CSARDependency dependency) {
        String archiveFileName = dependency.getName().concat("-").concat(dependency.getVersion()).concat(".csar");
        Path archivePath = localRepositoryPath.resolve(dependency.getName()).resolve(dependency.getVersion()).resolve(archiveFileName);

        ParsingContextExecution.Context previousContext = ParsingContextExecution.get();
        try {
            ParsingContextExecution.init();
            return toscaArchiveParser.parse(archivePath);
        } finally {
            ParsingContextExecution.destroy();
            if (previousContext != null) {
                ParsingContextExecution.set(previousContext);
            }
        }
    }
}