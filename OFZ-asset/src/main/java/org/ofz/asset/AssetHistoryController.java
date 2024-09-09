package org.ofz.asset;

import org.ofz.asset.dto.AssetHistoryRateRes;
import org.ofz.asset.entity.AssetHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asset")
public class AssetHistoryController {

    private final AssetHistoryService assetHistoryService;

    @Autowired
    public AssetHistoryController(AssetHistoryService assetHistoryService) {
        this.assetHistoryService = assetHistoryService;
    }

    // 특정 유저의 mortgage_sum 변동률 조회
    @GetMapping("/rate-of-change/{userId}")
    public ResponseEntity<Double> getMortgageSumRateOfChange(@PathVariable Long userId) {
        double rateOfChange = assetHistoryService.getMortgageSumRateOfChange(userId);
        return ResponseEntity.ok(rateOfChange);
    }

    // 전체 유저의 최신 mortgage_sum_rate_of_change 조회
    @GetMapping("/rate-of-change/all")
    public ResponseEntity<List<AssetHistory>> getAllLatestUserMarginRequirements() {
        List<AssetHistory> allLatestUserMarginRequirements = assetHistoryService.getAllLatestUserMarginRequirements();
        return ResponseEntity.ok(allLatestUserMarginRequirements);
    }

    // 모든 유저의 변동률을 계산하고 저장
    @PostMapping("/calculate-rate-of-change")
    public ResponseEntity<String> calculateRateOfChangeForAllUsers() {
        assetHistoryService.calculateAndSaveRateOfChangeForAllUsers();
        return ResponseEntity.ok("모든 유저의 변동률 계산이 완료되었습니다.");
    }

    @GetMapping("/rate-of-change/under")
    public ResponseEntity<List<AssetHistoryRateRes>> etAllByRateOfChangeLessThan(@RequestParam double limit) {
        List<AssetHistoryRateRes> result = assetHistoryService.getAllByRateOfChangeLessThan(limit);
        return ResponseEntity.ok(result);
    }
}
