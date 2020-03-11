package fr.fastmarketeam.pimnow.service;

import fr.fastmarketeam.pimnow.domain.Product;
import fr.fastmarketeam.pimnow.domain.Workflow;

public interface WorkflowService {
    Workflow linkProductToWorkflow(Workflow workflow, Product product);

}
