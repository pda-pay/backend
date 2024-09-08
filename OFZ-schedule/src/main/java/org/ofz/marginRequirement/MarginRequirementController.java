package org.ofz.marginRequirement;

import org.ofz.marginRequirement.entity.MarginRequirementHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // margin_requirement가 특정 값 이하인 유저 조회
    @GetMapping("/user-margin-requirements-under")
    public ResponseEntity<List<MarginRequirementHistory>> getUsersWithMarginRequirementUnder(
            @RequestParam(value = "limit", defaultValue = "160") int limit) {
        List<MarginRequirementHistory> usersUnderLimit = marginRequirementService.getUsersWithMarginRequirementUnder(limit);
        return ResponseEntity.ok(usersUnderLimit);
    }

    @PostMapping("/process-all-user-limits")
    public ResponseEntity<String> processAllUserLimits() {
        marginRequirementService.processAllUserLimits();
        return ResponseEntity.ok("모든 유저의 담보 한도 비율 계산이 완료되었습니다.");
    }
}
