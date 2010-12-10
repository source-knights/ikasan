/* 
 * $Id: 
 * $URL: 
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.framework.error.dao;

import java.util.List;

import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.management.search.PagedSearchResult;

/**
 * Data Access interface for the persistence of <code>ErrorOccurrence</code> instances
 * @author Ikasan Development Team
 *
 */
public interface ErrorOccurrenceDao {
	
	/**
	 * Persist an ErrorOccurrence
	 * 
	 * @param errorOccurrence
	 */
	public void save(ErrorOccurrence errorOccurrence);
	
	/**
	 * Retrieves an ErrorOccurrence by ids
	 * 
	 * @param id
	 * @return specified ErrorOccurrence
	 */
	public ErrorOccurrence getErrorOccurrence(Long id);

	
	/**
	 * Perform a paged search for <code>ErrorOccurrence</code>s
	 * 
	 * @param pageNo 
	 * @param pageSize
	 * @param orderBy
	 * @param orderAscending
	 * @param moduleName
	 * @param flowName
	 * 
	 * @return PagedSearchResult
	 */
	public PagedSearchResult<ErrorOccurrence> findErrorOccurrences(int pageNo, int pageSize, String orderBy, boolean orderAscending,String moduleName, String flowName);
	
    /**
     * Deletes all ErrorOccurrences that have surpassed their expiry
     */
    public void deleteAllExpired();

	/**
	 * Returns all ErrorOccurrences with the specified eventId
	 * 
	 * @param eventId
	 * @return List of ErrorOccurrence 
	 */
	public List<ErrorOccurrence> getErrorOccurrences(String eventId);
}
