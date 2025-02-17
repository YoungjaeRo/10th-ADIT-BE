package com.adit.backend.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import com.adit.backend.domain.event.entity.UserEvent;
import com.adit.backend.domain.place.entity.UserPlace;
import com.adit.backend.domain.user.enums.Role;
import com.adit.backend.domain.user.enums.SocialType;
import com.adit.backend.global.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	name = "user",
	indexes = @Index(name = "idx_user_email", columnList = "email")
)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 10)
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false, length = 50)
	private String nickname;

	@Column(nullable = false)
	private String profile;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SocialType socialType;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Role role;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserEvent> userEvents = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserPlace> userPlaces = new ArrayList<>();

	@OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Friendship> sentFriendRequests = new ArrayList<>();

	@OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Friendship> receivedFriendRequests = new ArrayList<>();

	@Builder
	public User(String name, String email, String nickname, String profile, SocialType socialType, Role role) {
		this.name = name;
		this.email = email;
		this.nickname = nickname;
		this.profile = profile;
		this.socialType = socialType;
		this.role = role;
	}

	// 연관관계 메서드
	public void addUserEvent(UserEvent userEvent) {
		this.userEvents.add(userEvent);
		userEvent.assignUser(this);
	}

	public void addUserPlace(UserPlace userPlace) {
		this.userPlaces.add(userPlace);
		userPlace.assignedUser(this);
	}

	public void addFriendRequest(Friendship friendship) {
		this.sentFriendRequests.add(friendship);
		friendship.setFromUser(this);
	}

	public void changeNickName(String nickName) {
		this.nickname = nickName;
	}

	public void decideSocialType() {
		this.socialType = SocialType.KAKAO;
	}

	public void updateRole() {
		this.role = Role.USER;
	}
}
