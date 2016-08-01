Feature: Topology editor: nodes templates

  Background:
    Given I am authenticated with "ADMIN" role
    And I upload CSAR from path "../../alien4cloud/target/it-artifacts/tosca-base-types-1.0.csar"
    And I upload CSAR from path "../../alien4cloud/target/it-artifacts/java-types-1.0.csar"
    And I create an empty topology template

  Scenario: Remove a nodetemplate in a topology
    Given I Rename the operation
      | type              | org.alien4cloud.tosca.editor.operations.nodetemplate.AddNodeOperation |
      | nodeName          | Template1                                                             |
      | indexedNodeTypeId | tosca.nodes.Compute:1.0                                               |
    When I execute the operation
      | type     | org.alien4cloud.tosca.editor.operations.nodetemplate.RenameNodeOperation |
      | nodeName | Template1                                                                |
      | newName  | Template2                                                                |
    Then No exception should be thrown
    And The SPEL int expression "nodeTemplates.size()" should return 1
    And The SPEL int expression "nodeTemplates['Template1']" should return null
    And The SPEL int expression "nodeTemplates['Template1'].type" should return "tosca.nodes.Compute"

  Scenario: Rename a non existing nodetemplate in an empty topology should fail
    When I execute the operation
      | type     | org.alien4cloud.tosca.editor.operations.nodetemplate.RenameNodeOperation |
      | nodeName | missingNode                                                              |
      | newName  | Template2                                                                |
    Then an exception of type "alien4cloud.exception.NotFoundException" should be thrown

  Scenario: Remove a non existing nodetemplate in a topology should fail
    Given I execute the operation
      | type              | org.alien4cloud.tosca.editor.operations.nodetemplate.AddNodeOperation |
      | nodeName          | Template1                                                             |
      | indexedNodeTypeId | tosca.nodes.Compute:1.0                                               |
    When I execute the operation
      | type     | org.alien4cloud.tosca.editor.operations.nodetemplate.RenameNodeOperation |
      | nodeName | missingNode                                                              |
      | newName  | Template2                                                                |
    Then an exception of type "alien4cloud.exception.NotFoundException" should be thrown