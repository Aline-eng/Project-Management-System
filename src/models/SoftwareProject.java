package models;

public class SoftwareProject extends Project {

    private String primaryLanguage;

    public SoftwareProject(String name, String description, double budget, int teamSize, String primaryLanguage) {
        super(name, description, budget, teamSize);
        this.primaryLanguage = primaryLanguage;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    @Override
    public String getProjectDetails() {
        return "Primary Language: " + primaryLanguage;
    }

    @Override
    public String getProjectType() {
        return "Software";
    }
}
