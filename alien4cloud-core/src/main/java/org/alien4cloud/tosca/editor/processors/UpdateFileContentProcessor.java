package org.alien4cloud.tosca.editor.processors;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.alien4cloud.tosca.editor.operations.UpdateFileContentOperation;
import org.springframework.stereotype.Component;

/**
 * Process an {@link org.alien4cloud.tosca.editor.operations.UpdateFileContentOperation} to update the content of a file.
 */
@Component
public class UpdateFileContentProcessor extends AbstractUpdateFileProcessor<UpdateFileContentOperation> {
    @Override
    public void process(UpdateFileContentOperation operation) {
        if (operation.getTempFileId() == null) {
            operation.setArtifactStream(new ByteArrayInputStream(operation.getContent().getBytes(StandardCharsets.UTF_8)));
        }
        super.process(operation);
        // content is store in a temp file on disk, no need to keep data in memory.
        operation.setContent(null);
    }
}