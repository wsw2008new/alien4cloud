package org.alien4cloud.tosca.editor.processors.nodetemplate;

import alien4cloud.model.topology.Topology;
import alien4cloud.topology.TopologyServiceCore;
import org.alien4cloud.tosca.editor.EditionContextManager;

import alien4cloud.model.topology.NodeTemplate;
import org.alien4cloud.tosca.editor.operations.nodetemplate.AbstractNodeOperation;
import org.alien4cloud.tosca.editor.processors.IEditorOperationProcessor;

import java.util.Map;

/**
 * Abstract operation to get a required node template.
 */
public abstract class AbstractNodeProcessor<T extends AbstractNodeOperation> implements IEditorOperationProcessor<T> {
    @Override
    public void process(T operation) {
        Topology topology = EditionContextManager.getTopology();
        Map<String, NodeTemplate> nodeTemplates = TopologyServiceCore.getNodeTemplates(topology);
        NodeTemplate nodeTemplate = TopologyServiceCore.getNodeTemplate(topology.getId(), operation.getNodeName(), nodeTemplates);

        processNodeOperation(operation, nodeTemplate);
    }

    protected abstract void processNodeOperation(T operation, NodeTemplate nodeTemplate);
}
