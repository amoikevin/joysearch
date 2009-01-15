/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.joy.index.service;

import org.joy.group.service.JobResource;
import org.joy.group.service.Job;

/**
 *
 * @author Lamfeeling
 */
public class IndexJob extends Job{
    private String URL;
    private JobResource indexInfo;
    private JobResource doc;
    
    public IndexJob(String URL, JobResource hits) {
        this.URL = URL;
        this.indexInfo = hits;
    }

    public IndexJob(String URL, JobResource hits, JobResource doc) {
        this.URL = URL;
        this.indexInfo = hits;
        this.doc = doc;
    }

    public JobResource getDocument() {
        return doc;
    }

    public JobResource getHits() {
        return indexInfo;
    }

    public String getURL() {
        return URL;
    }
}
