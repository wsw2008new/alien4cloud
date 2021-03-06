package org.alien4cloud.tosca.editor.processors.relationshiptemplate;

import alien4cloud.model.components.AbstractPropertyValue;
import alien4cloud.model.components.IndexedRelationshipType;
import alien4cloud.model.topology.NodeTemplate;
import alien4cloud.model.topology.RelationshipTemplate;
import alien4cloud.model.topology.Topology;
import alien4cloud.paas.wf.WorkflowsBuilderService;
import alien4cloud.topology.TopologyService;
import alien4cloud.tosca.context.ToscaContext;
import alien4cloud.tosca.topology.NodeTemplateBuilder;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.alien4cloud.tosca.editor.EditionContextManager;
import org.alien4cloud.tosca.editor.operations.relationshiptemplate.RebuildRelationshipOperation;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Process a {@link RebuildRelationshipOperation}.
 *
 * Rebuild a relationship template, synching it with the {@link alien4cloud.model.components.IndexedRelationshipType}
 * present at the moment in the repository
 */
@Slf4j
@Component
public class RebuildRelationshipProcessor extends AbstractRelationshipProcessor<RebuildRelationshipOperation> {
    @Resource
    private TopologyService topologyService;
    @Resource
    private WorkflowsBuilderService workflowBuilderService;

    @Override
    protected void processRelationshipOperation(RebuildRelationshipOperation operation, NodeTemplate nodeTemplate, RelationshipTemplate relationshipTemplate) {
        Topology topology = EditionContextManager.getTopology();
        // rebuild a relationship template based on the current relationship type
        log.debug("Rebuilding the relationship <{}> in the node template <{}> of topology <{}> .", operation.getRelationshipName(), operation.getNodeName(),
                topology.getId());
        IndexedRelationshipType relType = ToscaContext.getOrFail(IndexedRelationshipType.class, relationshipTemplate.getType());
        Map<String, AbstractPropertyValue> properties = Maps.newHashMap();
        NodeTemplateBuilder.fillProperties(properties, relType.getProperties(), relationshipTemplate.getProperties());
        relationshipTemplate.setProperties(properties);
        relationshipTemplate.setAttributes(relType.getAttributes());
    }
}