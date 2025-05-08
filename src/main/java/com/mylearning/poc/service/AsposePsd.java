package com.mylearning.poc.service;

import com.mylearning.poc.dto.PsdGenerationRequest;
import com.mylearning.poc.dto.PsdGenerationResponse;


public interface AsposePsd {
    public PsdGenerationResponse generatePsdFromInput(PsdGenerationRequest request) throws Exception;
}
