/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.index.core;

import org.joy.group.core.Job;

/**
 *
 * @author Lamfeeling
 */
public class RankUpdateJob extends Job {

    private String URL;
    float rank;

    public float getRank() {
        return rank;
    }

    public String getURL() {
        return URL;
    }

    public RankUpdateJob(String URL, float rank) {
        this.URL = URL;
        this.rank = rank;
    }

    public RankUpdateJob() {
    }
}
