package com.jenkins.job.firstjenkinsjob.model;

public class ApiResponse {
    private String type;
    private String setup;
    private String punchline;
    private int id;

    public ApiResponse() {

    }

    // Getters and setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSetup() { return setup; }
    public void setSetup(String setup) { this.setup = setup; }

    public String getPunchline() { return punchline; }
    public void setPunchline(String punchline) { this.punchline = punchline; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
