package org.ofz.marginRequirement;

import org.ofz.marginRequirement.entity.MarginRequirementHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class MarginRequirementController {

    private final MarginRequirementService marginRequirementService;

    @Autowired
    public MarginRequirementController(MarginRequirementService marginRequirementService) {
        this.marginRequirementService = marginRequirementService;
    }

    // 전체 유저의 margin requirement 조회
    @GetMapping("/all-user-margin-requirements")
    public ResponseEntity<List<MarginRequirementHistory>> getAllUserMarginRequirements() {
        List<MarginRequirementHistory> marginRequirements = marginRequirementService.getAllUserMarginRequirements();
        return ResponseEntity.ok(marginRequirements);
    }

    @PostMapping("/process-all-user-limits")
    public ResponseEntity<String> processAllUserLimits() {
        marginRequirementService.processAllUserLimits();
        return ResponseEntity.ok("모든 유저의 담보 한도 비율 계산이 완료되었습니다.");
    }
}
