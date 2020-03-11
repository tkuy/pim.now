package fr.fastmarketeam.pimnow.service.impl;

import fr.fastmarketeam.pimnow.domain.Product;
import fr.fastmarketeam.pimnow.domain.Workflow;
import fr.fastmarketeam.pimnow.repository.WorkflowRepository;
import fr.fastmarketeam.pimnow.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WorkflowServiceImpl implements WorkflowService {

    private final Logger log = LoggerFactory.getLogger(WorkflowServiceImpl.class);

    private WorkflowRepository workflowRepository;

    @Override
    public Workflow linkProductToWorkflow(Workflow workflow, Product product) {
        return null;
    }
}
