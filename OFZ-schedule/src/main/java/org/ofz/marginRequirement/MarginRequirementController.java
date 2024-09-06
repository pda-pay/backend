package org.ofz.marginRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedule")
public class MarginRequirementController {

    private final MarginRequirementService marginRequirementService;

    @Autowired
    public MarginRequirementController(MarginRequirementService marginRequirementService) {
        this.marginRequirementService = marginRequirementService;
    }

    @PostMapping("/process-all-user-limits")
    public ResponseEntity<String> processAllUserLimits() {
        marginRequirementService.processAllUserLimits();
        return ResponseEntity.ok("모든 유저의 담보 한도 비율 계산이 완료되었습니다.");
    }
}
