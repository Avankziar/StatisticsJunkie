package me.avankziar.sj.general.objects;

import java.util.List;

import org.bukkit.Material;

public class FileAchievementGoal 
{
	private String achievementGoalUniqueName;
	private String displayName;
	private StatisticType statisticType;
	private String materialEntityType;
	private long statisticValue;
	private List<String> executeCommand;
	private boolean broadcast;
	private List<String> broadcastMessage;
	private int guiSlot;
	private Material displayItemMaterial;
	private String displayItemDisplayName;
	private List<String> displayItemLore;
	private boolean displayItemEnchantmentGlintOverride;
	private Material displayItemIfNotAchievedMaterial;
	private String displayItemIfNotAchievedDisplayName;
	private List<String> displayItemIfNotAchievedLore;
	private boolean displayItemIfNotAchievedEnchantmentGlintOverride;
	
	public FileAchievementGoal(String achievementGoalUniqueName, String displayName,
			StatisticType statisticType, String materialEntityType, long statisticValue,
			List<String> executeCommand, boolean broadcast, List<String> broadcastMessage,
			int guiSlot,
			Material displayItemMaterial,
			String displayItemDisplayName,
			List<String> displayItemLore,
			boolean displayItemEnchantmentGlintOverride,
			Material displayItemIfNotAchievedMaterial,
			String displayItemIfNotAchievedDisplayName,
			List<String> displayItemIfNotAchievedLore,
			boolean displayItemIfNotAchievedEnchantmentGlintOverride)
	{
		setAchievementGoalUniqueName(achievementGoalUniqueName);
		setDisplayName(displayName);
		setStatisticType(statisticType);
		setMaterialEntityType(materialEntityType);
		setStatisticValue(statisticValue);
		setExecuteCommand(executeCommand);
		setBroadcast(broadcast);
		setBroadcastMessage(broadcastMessage);
		setGuiSlot(guiSlot);
		setDisplayItemMaterial(displayItemMaterial);
		setDisplayItemDisplayName(displayItemDisplayName);
		setDisplayItemLore(displayItemLore);
		setDisplayItemEnchantmentGlintOverride(displayItemEnchantmentGlintOverride);
		setDisplayItemIfNotAchievedMaterial(displayItemIfNotAchievedMaterial);
		setDisplayItemIfNotAchievedDisplayName(displayItemIfNotAchievedDisplayName);
		setDisplayItemIfNotAchievedLore(displayItemIfNotAchievedLore);
		setDisplayItemIfNotAchievedEnchantmentGlintOverride(displayItemIfNotAchievedEnchantmentGlintOverride);
	}

	public String getAchievementGoalUniqueName() {
		return achievementGoalUniqueName;
	}

	public void setAchievementGoalUniqueName(String achievementGoalUniqueName) {
		this.achievementGoalUniqueName = achievementGoalUniqueName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public StatisticType getStatisticType() {
		return statisticType;
	}

	public void setStatisticType(StatisticType statisticType) {
		this.statisticType = statisticType;
	}

	public String getMaterialEntityType() {
		return materialEntityType;
	}

	public void setMaterialEntityType(String materialEntityType) {
		this.materialEntityType = materialEntityType;
	}

	public long getStatisticValue() {
		return statisticValue;
	}

	public void setStatisticValue(long statisticValue) {
		this.statisticValue = statisticValue;
	}

	public List<String> getExecuteCommand() {
		return executeCommand;
	}

	public void setExecuteCommand(List<String> executeCommand) {
		this.executeCommand = executeCommand;
	}

	public boolean isBroadcast() {
		return broadcast;
	}

	public void setBroadcast(boolean broadcast) {
		this.broadcast = broadcast;
	}

	public List<String> getBroadcastMessage() {
		return broadcastMessage;
	}

	public void setBroadcastMessage(List<String> broadcastMessage) {
		this.broadcastMessage = broadcastMessage;
	}

	public int getGuiSlot() {
		return guiSlot;
	}

	public void setGuiSlot(int guiSlot) {
		this.guiSlot = guiSlot;
	}

	public Material getDisplayItemMaterial() {
		return displayItemMaterial;
	}

	public void setDisplayItemMaterial(Material displayItemMaterial) {
		this.displayItemMaterial = displayItemMaterial;
	}

	public String getDisplayItemDisplayName() {
		return displayItemDisplayName;
	}

	public void setDisplayItemDisplayName(String displayItemDisplayName) {
		this.displayItemDisplayName = displayItemDisplayName;
	}

	public List<String> getDisplayItemLore() {
		return displayItemLore;
	}

	public void setDisplayItemLore(List<String> displayItemLore) {
		this.displayItemLore = displayItemLore;
	}

	public boolean isDisplayItemEnchantmentGlintOverride() {
		return displayItemEnchantmentGlintOverride;
	}

	public void setDisplayItemEnchantmentGlintOverride(boolean displayItemEnchantmentGlintOverride) {
		this.displayItemEnchantmentGlintOverride = displayItemEnchantmentGlintOverride;
	}

	public Material getDisplayItemIfNotAchievedMaterial() {
		return displayItemIfNotAchievedMaterial;
	}

	public void setDisplayItemIfNotAchievedMaterial(Material displayItemIfNotAchievedMaterial) {
		this.displayItemIfNotAchievedMaterial = displayItemIfNotAchievedMaterial;
	}

	public String getDisplayItemIfNotAchievedDisplayName() {
		return displayItemIfNotAchievedDisplayName;
	}

	public void setDisplayItemIfNotAchievedDisplayName(String displayItemIfNotAchievedDisplayName) {
		this.displayItemIfNotAchievedDisplayName = displayItemIfNotAchievedDisplayName;
	}

	public List<String> getDisplayItemIfNotAchievedLore() {
		return displayItemIfNotAchievedLore;
	}

	public void setDisplayItemIfNotAchievedLore(List<String> displayItemIfNotAchievedLore) {
		this.displayItemIfNotAchievedLore = displayItemIfNotAchievedLore;
	}

	public boolean isDisplayItemIfNotAchievedEnchantmentGlintOverride() {
		return displayItemIfNotAchievedEnchantmentGlintOverride;
	}

	public void setDisplayItemIfNotAchievedEnchantmentGlintOverride(
			boolean displayItemIfNotAchievedEnchantmentGlintOverride) {
		this.displayItemIfNotAchievedEnchantmentGlintOverride = displayItemIfNotAchievedEnchantmentGlintOverride;
	}
}