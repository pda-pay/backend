package org.ofz.asset;

import org.ofz.asset.dto.AssetHistoryLast10DaysRes;
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

    // 담보총액 변동율이 특정 값 이하인 유저 조회
    @GetMapping("/rate-of-change/under")
    public ResponseEntity<List<AssetHistoryRateRes>> etAllByRateOfChangeLessThan(@RequestParam double limit) {
        List<AssetHistoryRateRes> result = assetHistoryService.getAllByRateOfChangeLessThan(limit);
        return ResponseEntity.ok(result);
    }

    // 특정 유저의 어제부터 어제-10일 전까지 10개의 데이터 조회
    @GetMapping("/history/last-10-days")
    public ResponseEntity<List<AssetHistoryLast10DaysRes>> getLast10DaysData(@RequestHeader("X-USER-ID") Long userId) {
        List<AssetHistoryLast10DaysRes> data = assetHistoryService.getLast10DaysData(userId);
        return ResponseEntity.ok(data);
    }

    // marginRequirement가 특정 limit 이하인 유저 조회 API
    @GetMapping("/margin-requirement/under")
    public ResponseEntity<List<AssetHistoryRateRes>> getAllByMarginRequirementLessThan(@RequestParam int limit) {
        List<AssetHistoryRateRes> results = assetHistoryService.getAllByMarginRequirementLessThan(limit);
        return ResponseEntity.ok(results);
    }
}