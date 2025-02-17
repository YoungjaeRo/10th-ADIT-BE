package com.adit.backend.domain.place.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adit.backend.domain.place.dto.request.PlaceRequestDto;
import com.adit.backend.domain.place.dto.response.PlaceResponseDto;
import com.adit.backend.domain.place.service.command.CommonPlaceCommandService;
import com.adit.backend.domain.place.service.command.UserPlaceCommandService;
import com.adit.backend.domain.place.service.query.CommonPlaceQueryService;
import com.adit.backend.domain.place.service.query.UserPlaceQueryService;
import com.adit.backend.domain.user.entity.User;
import com.adit.backend.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Validated
@Tag(name = "Place API", description = "장소 생성, 수정, 삭제, 조회할 수 있는 API 입니다")
public class PlaceController {

	private final CommonPlaceCommandService commonPlaceCommandService;
	private final CommonPlaceQueryService commonPlaceQueryService;
	private final UserPlaceCommandService userPlaceCommandService;
	private final UserPlaceQueryService userPlaceQueryService;

	// 장소 생성 API
	@Operation(summary = "장소 생성", description = "카카오 맵 키워드 검색 후 CommonPlace, UserPlace 에 장소를 저장합니다")
	@PostMapping()
	public ResponseEntity<ApiResponse<PlaceResponseDto>> createPlace(
		@Valid @RequestBody PlaceRequestDto requestDto, @AuthenticationPrincipal(expression = "user") User user) {
		PlaceResponseDto userPlace = userPlaceCommandService.createUserPlace(user.getId(), requestDto);
		// 생성된 장소를 응답으로 반환
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userPlace));
	}

	// 장소 수정 API
	@Operation(summary = "장소 수정", description = "Commonplace 의 장소를 수정합니다")
	@PutMapping("/{placeId}")
	public ResponseEntity<ApiResponse<PlaceResponseDto>> updatePlace(@PathVariable @Min(1) Long placeId,
		@Valid @RequestBody PlaceRequestDto requestDto) {
		// ID로 기존 장소를 찾아 수정
		PlaceResponseDto commonPlace = commonPlaceCommandService.updatePlace(placeId, requestDto);
		// 수정된 장소를 응답으로 반환
		return ResponseEntity.ok(ApiResponse.success(commonPlace));
	}

	// 장소 삭제 API
	@Operation(summary = "장소 삭제", description = "userPlaceId에 해당하는 장소 삭제")
	@DeleteMapping("/{userPlaceId}")
	public ResponseEntity<ApiResponse<String>> deletePlace(@PathVariable @Min(1) Long userPlaceId) {
		// ID로 장소를 삭제
		userPlaceCommandService.deletePlace(userPlaceId);
		// 삭제 완료 메시지 응답
		return ResponseEntity.ok(ApiResponse.success("Place deleted successfully"));
	}

	// 카테고리 기반으로 장소 찾기 API
	@Operation(summary = "카테고리로 장소 조회", description = "userId에 해당하는 사용자가 가진 장소 중 특정 카테고리에 해당하는 장소 조회")
	@GetMapping("/category")
	public ResponseEntity<ApiResponse<List<PlaceResponseDto>>> getPlaceByCategory(@RequestParam List<String> subCategory
		, @AuthenticationPrincipal(expression = "user") User user) {

		List<PlaceResponseDto> placeByCategory = userPlaceQueryService.getPlaceByCategory(subCategory, user.getId());

		return ResponseEntity.ok(ApiResponse.success(placeByCategory));
	}

	//인기 기반으로 장소 찾기 API
	@Operation(summary = "인기순으로 장소 조회", description = "전체 장소 중 bookmarkCount 가 높은 순서대로 1~5위 장소 조회")
	@GetMapping("/popular")
	public ResponseEntity<ApiResponse<List<PlaceResponseDto>>> getPlaceByPopular() {
		List<PlaceResponseDto> placeByPopular = commonPlaceQueryService.getPlaceByPopular();
		return ResponseEntity.ok(ApiResponse.success(placeByPopular));
	}

	//저장된 장소 찾기 API
	@Operation(summary = "저장된 장소 조회", description = "userId에 해당하는 사용자가 저장한 장소 조회")
	@GetMapping()
	public ResponseEntity<ApiResponse<List<PlaceResponseDto>>> getSavedPlace(
		@AuthenticationPrincipal(expression = "user") User user) {
		List<PlaceResponseDto> savedPlace = userPlaceQueryService.getSavedPlace(user.getId());
		return ResponseEntity.ok(ApiResponse.success(savedPlace));
	}

	//특정 장소 상세 정보 찾기 API
	@Operation(summary = "특정 장소 상세 정보 조회", description = "해당 placeName(상호명)을 가진 장소 조회")
	@GetMapping("/detail")
	public ResponseEntity<ApiResponse<PlaceResponseDto>> getDetailedPlace(@RequestParam String placeName) {

		PlaceResponseDto detailedPlace = commonPlaceQueryService.getDetailedPlace(placeName);
		return ResponseEntity.ok(ApiResponse.success(detailedPlace));
	}

	//현재 위치 기반 장소 찾기 API
	@Operation(summary = "사용자 위치로 장소 조회", description = "userId에 해당하는 사용자가 가진 장소 중 사용자의 위치와 가까운 순으로 장소 조회")
	@GetMapping("/location")
	public ResponseEntity<ApiResponse<List<PlaceResponseDto>>> getPlaceByLocation(
		@RequestParam @DecimalMin("33.0") @DecimalMax("43.0") double latitude
		, @RequestParam @DecimalMin("124.0") @DecimalMax("132.0") double longitude,
		@AuthenticationPrincipal(expression = "user") User user) {
		List<PlaceResponseDto> placeByLocation = userPlaceQueryService.getPlaceByLocation(latitude, longitude,
			user.getId());
		return ResponseEntity.ok(ApiResponse.success(placeByLocation));
	}

	//주소 기반 장소 찾기 API
	@Operation(summary = "주소로 장소 조회", description = "userId에 해당하는 사용자가 가진 장소 중 address 를 포함하고 있는 장소 조회")
	@GetMapping("/address")
	public ResponseEntity<ApiResponse<List<PlaceResponseDto>>> getPlaceByAddress(@RequestParam List<String> address
		, @AuthenticationPrincipal(expression = "user") User user) {

		List<PlaceResponseDto> placeByAddress = userPlaceQueryService.getPlaceByAddress(address, user.getId());
		return ResponseEntity.ok(ApiResponse.success(placeByAddress));
	}

	//장소 방문 여부 표시 API
	@Operation(summary = "장소 방문 표시", description = "userPlaceId에 해당하는 장소 방문 표시")
	@PutMapping("/{userPlaceId}/visit")
	public ResponseEntity<ApiResponse<String>> checkVisitedPlace(@PathVariable Long userPlaceId) {
		userPlaceCommandService.checkVisitedPlace(userPlaceId);
		return ResponseEntity.ok(ApiResponse.success("visit sign successfully"));
	}

	//친구 기반 장소 찾기 API
	@Operation(summary = "친구 장소 조회", description = "userId에 해당하는 사용자의 친구가 저장한 장소 조회")
	@GetMapping("/friend")
	public ResponseEntity<ApiResponse<List<PlaceResponseDto>>> getPlaceByFriend(
		@AuthenticationPrincipal(expression = "user") User user) {
		List<PlaceResponseDto> placeByFriend = userPlaceQueryService.getPlaceByFriend(user.getId());
		return ResponseEntity.ok(ApiResponse.success(placeByFriend));
	}

	//장소 메모 수정 API
	@Operation(summary = "장소 메모 수정", description = "userPlaceId에 해당하는 장소의 메모를 수정")
	@PutMapping("/{userPlaceId}/memo")
	public ResponseEntity<ApiResponse<PlaceResponseDto>> updateUserPlace(@PathVariable Long userPlaceId,
		@RequestParam String memo) {

		PlaceResponseDto updateUserPlace = userPlaceCommandService.updateUserPlace(userPlaceId, memo);
		return ResponseEntity.ok(ApiResponse.success(updateUserPlace));
	}
}
