package org.debugroom.sample.spring.security.common.apinfra.cloud.aws;

import java.util.List;
import java.util.Optional;

import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.Export;
import com.amazonaws.services.cloudformation.model.ListExportsRequest;
import com.amazonaws.services.cloudformation.model.ListExportsResult;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.core.env.ResourceIdResolver;
import org.springframework.stereotype.Component;

@Component
public class CloudFormationStackResolver implements InitializingBean {

    @Autowired
    AmazonCloudFormationClient amazonCloudFormationClient;

    @Autowired(required = false)
    ResourceIdResolver resourceIdResolver;

    private List<Export> exportList;

    public String getExportValue(String exportName){
        Optional<Export> targetExport = exportList.stream()
                .filter(export -> export.getName().equals(exportName))
                .findFirst();
        return targetExport.isPresent() ? targetExport.get().getValue() : null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ListExportsResult listExportsResult = amazonCloudFormationClient
                .listExports(new ListExportsRequest());
        this.exportList = listExportsResult.getExports();
    }
}
