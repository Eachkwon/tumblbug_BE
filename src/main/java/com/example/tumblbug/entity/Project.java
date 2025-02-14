package com.example.tumblbug.entity;

import com.example.tumblbug.dto.ProjectRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String category;

    private String summary;

    private String title;

    @OneToMany(mappedBy = "project")
    private List<Thumbnail> thumbnails;

    private Integer goal;

    private LocalDate startDate;

    private LocalDate endDate;

    @OneToMany(mappedBy = "project")
    private List<Reward> rewards;

    @Lob
    private String plan;

    private String creatorName;

    private String creatorBiography;

    private int totalFundingPrice;

    private int fundingCount;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public Project(ProjectRequestDto projectRequestDto, User user) {
        validateDates(projectRequestDto.getStartDate(), projectRequestDto.getEndDate());
        this.category = projectRequestDto.getCategory();
        this.summary = projectRequestDto.getSummary();
        this.title = projectRequestDto.getTitle();
        this.thumbnails = projectRequestDto.getThumbnails().stream()
                .map((thumbnail) -> new Thumbnail(thumbnail, this))
                .collect(Collectors.toList());
        this.goal = projectRequestDto.getGoal();
        this.startDate = projectRequestDto.getStartDate();
        this.endDate = projectRequestDto.getEndDate();
        this.rewards = projectRequestDto.getRewards().stream()
                .map((rewardRequestDto) -> new Reward(rewardRequestDto, this))
                .collect(Collectors.toList());
        this.plan = projectRequestDto.getPlan();
        this.creatorName = projectRequestDto.getCreatorName();
        this.creatorBiography = projectRequestDto.getCreatorBiography();
        this.totalFundingPrice = 0;
        this.fundingCount = 0;
        this.user = user;
    }

    public void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "시작일은 오늘 이후여야 합니다");
        }
        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "종료일은 시작일 이후여야 합니다");
        }
    }

    public void addFund(Reward reward) {
        totalFundingPrice += reward.getFundingPrice();
        fundingCount++;
    }

}
