package com.studentplanner.model;

public class GamificationReward {

    private int id;
    private String sourceType;
    private int sourceId;
    private String rewardType;
    private int xpAmount;
    private String awardedAt;

    public GamificationReward(
            String sourceType,
            int sourceId,
            String rewardType,
            int xpAmount,
            String awardedAt
    ) {
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.rewardType = rewardType;
        this.xpAmount = xpAmount;
        this.awardedAt = awardedAt;
    }

    public GamificationReward(
            int id,
            String sourceType,
            int sourceId,
            String rewardType,
            int xpAmount,
            String awardedAt
    ) {
        this.id = id;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.rewardType = rewardType;
        this.xpAmount = xpAmount;
        this.awardedAt = awardedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public int getXpAmount() {
        return xpAmount;
    }

    public void setXpAmount(int xpAmount) {
        this.xpAmount = xpAmount;
    }

    public String getAwardedAt() {
        return awardedAt;
    }

    public void setAwardedAt(String awardedAt) {
        this.awardedAt = awardedAt;
    }
}