/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/gunterze/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2013
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package org.dcm4chee.arr.cdi.query.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.dcm4chee.arr.cdi.query.IAuditRecordQueryBean.AbstractAuditRecordQueryDecorator;
import org.dcm4chee.arr.cdi.query.simple.SimpleQueryUtils.ClassifiedString;
import org.dcm4chee.arr.cdi.query.utils.QueryUtils.DateRange;
import org.dcm4chee.arr.entities.QCode;

import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.types.ExpressionUtils;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;


/**
 * 
 *
 */
public class SimpleQueryDecorator extends AbstractAuditRecordQueryDecorator 
{
	private DateRange dateRange;
	
	private List<ClassifiedString> types;
	
	private List<ClassifiedString> subtypes;
	
	private List<Integer> outcomes;
	
	private List<String> actions;
	
	private String aet;
	
	private String patientId;
	
	private String studyUid;
	
	private String accNr;
	
	private String userId;
	
	private String host;
	
	
	public SimpleQueryDecorator()
	{
	}
	
	public SimpleQueryDecorator setDateRange( DateRange dateRange )
	{
		this.dateRange = dateRange;
		return this;
	}
	
	public SimpleQueryDecorator setTypes( List<ClassifiedString> types )
	{
		this.types = types;
		return this;
	}
	
	public SimpleQueryDecorator setSubTypes( List<ClassifiedString> subtypes )
	{
		this.subtypes = subtypes;
		return this;
	}
	
	public SimpleQueryDecorator setOutcomes( List<Integer> outcomes )
	{
		this.outcomes = outcomes;
		return this;
	}
	
	public SimpleQueryDecorator setActions( List<String> actions )
	{
		this.actions = actions;
		return this;
	}
	
	public SimpleQueryDecorator setAET( String aet )
	{
		this.aet = aet;
		return this;
	}
	
	public SimpleQueryDecorator setPatientId( String patientId )
	{
		this.patientId = patientId;
		return this;
	}
	
	public SimpleQueryDecorator setStudyUid( String studyUid )
	{
		this.studyUid = studyUid;
		return this;
	}
	
	public SimpleQueryDecorator setAccNr( String accNr )
	{
		this.accNr = accNr;
		return this;
	}
	
	public SimpleQueryDecorator setUserId( String userId )
	{
		this.userId = userId;
		return this;
	}
	
	public SimpleQueryDecorator setHost( String host )
	{
		this.host = host;
		return this;
	}

	@Override
	public SimpleQueryDecorator setMaxResults( Long maxResults )
	{
		super.setMaxResults( maxResults );
		return this;
	}
		
	@Override
	public Predicate getEventTypePredicate()
	{
		return toExpression( ar.eventType, subtypes );
	}
	
	@Override
	public Predicate getEventIDPredicate()
	{
		return toExpression( ar.eventType, types );
	}
	
	@Override
	public boolean isOptimizedCountQueryPossible()
	{
		return studyUid == null || patientId == null;
	}
	
	@Override
	public List<Predicate> getAuditRecordPredicates()
	{
		List<Predicate> predicates = new ArrayList<>(8);

		// date range
		if ( dateRange != null )
		{
			Date start = dateRange.getStartDate();
			Date end = dateRange.getEndDate();
			
			if ( start!=null && end!=null )
			{
				predicates = addIgnoreNull( predicates, ar.eventDateTime.between(start,end) );
			}
			else if ( start != null )
			{
				predicates = addIgnoreNull( predicates, ar.eventDateTime.goe( start ) );
			}
			else if ( end != null )
			{
				predicates = addIgnoreNull( predicates, ar.eventDateTime.loe( end ) );
			}
		}

		// types
		predicates = addIgnoreNull( predicates, toExpression( eventId, this.types ) );

		// subtypes
		predicates = addIgnoreNull( predicates, toExpression( eventType, this.subtypes ) );

		// outcomes
		if ( !emptyOrNull( this.outcomes ) )
		{
			predicates = addIgnoreNull( predicates, ar.eventOutcome.in( this.outcomes ) );
		}
		
		// actions
		if ( !emptyOrNull( this.actions ) )
		{
			predicates = addIgnoreNull( predicates, ar.eventAction.in( this.actions ) );
		}

		return predicates != null ?
				Collections.singletonList( ExpressionUtils.allOf( predicates ) ) : null;
	}
	
	@Override
	public List<Predicate> getActiveParticipantPredicates()
	{
		List<Predicate> predicates = null;
				
		// user id
		if ( userId != null )
		{
			predicates = addIgnoreNull( predicates,
					ap.userID.containsIgnoreCase( userId )
			);
		}

		// host
		if ( host != null )
		{
			predicates = addIgnoreNull( predicates,
				ap.networkAccessPointID.containsIgnoreCase( host ).and(
						ap.networkAccessPointType.notIn( 3, 4, 5 )
				)
			);
		}
		
		// aet
		if ( aet != null )
		{
			predicates = addIgnoreNull( predicates, 
				ap.auditRecord.sourceID.containsIgnoreCase( aet ).or(
					ap.alternativeUserID.containsIgnoreCase( aet ) )
			);
		}

		return predicates != null ? 
				Collections.singletonList( ExpressionUtils.allOf(predicates) ) : null;
	}
	
	@Override
	public List<Predicate> getParticipantObjectPredicates()
	{
		List<Predicate> predicates = null;

		// accNr
		/* Not supported yet:
		 * 
		if ( accNr != null )
		{
			predicates = addIgnoreNull( predicates,
				po.objectIDType.value.eq("2") // system object and
				.and(
					po.objectRole.isNull().or( // empty or report or resource
						po.objectRole.in(3,4) 
					)
				).and(
					po.objectID.equalsIgnoreCase( accNr ).or(
						po.objectName.equalsIgnoreCase( accNr ) 
					)
				) 
			);
		}
		 */
		
		// study UID
		if ( studyUid != null )
		{
			predicates = addIgnoreNull( predicates, po.objectIDType.value.eq("110180").and(
					po.objectID.eq( studyUid ) ) );
		}

		// patient id
		if ( patientId != null )
		{
			predicates = addIgnoreNull( predicates,
					po.objectID.containsIgnoreCase( patientId ).and(
							po.objectRole.eq(1) ) );
		}

		return predicates;
	}
		
	@Override
	public List<Predicate> getAllPredicates()
	{
		List<Predicate> predicates = new ArrayList<>();
		
		// add audit-record predicates if present
		List<Predicate> arPredicates = getAuditRecordPredicates();
		if ( arPredicates != null )
		{
			for ( Predicate p : arPredicates )
			{
				predicates.add( p );
			}
		}

		// subquery for matching participant objects
		List<Predicate> poPredicates = getParticipantObjectPredicates();
		if ( !emptyOrNull( poPredicates ) )
		{
			for ( Predicate p : poPredicates )
			{
				predicates = addIgnoreNull( predicates, new JPASubQuery()
					.from( po )
					.join( po.auditRecord )
					.leftJoin( po.objectIDType )
					.where( po.auditRecord.pk.eq(ar.pk), p )
					.exists()
				);
			}
		}
		
		// subquery for matching active participants
		List<Predicate> apPredicates = getActiveParticipantPredicates();
		if ( !emptyOrNull( apPredicates ) )
		{
			for( Predicate p : apPredicates )
			{				
				predicates = addIgnoreNull( predicates, new JPASubQuery()
					.from( ap )
					.join( ap.auditRecord )
					.leftJoin( ap.roleID )
					.where( ap.auditRecord.pk.eq(ar.pk), p )
					.exists()
				);
			}
		}

		return predicates;
	}
	
	private static BooleanExpression toExpression( QCode path, List<ClassifiedString> values )
	{
		if ( !emptyOrNull( values ) )
		{
			BooleanExpression e = toExpression( path, values.get(0) );
			if ( values.size() > 1 )
			{
				for (int i=1; i<values.size(); i++ )
				{
					e = e.or( toExpression( path, values.get(i) ) );
				}
			}
			return e;
		}
		return null;
	}
	
	private static BooleanExpression toExpression( QCode path, ClassifiedString value )
	{
		if ( value != null )
		{
			BooleanExpression exp = path.value.containsIgnoreCase( value.getValue() );
			if ( value.isClassified() )
			{
				exp = exp.and( path.designator.containsIgnoreCase( value.getDesignator() ) );
			}
			return exp;
		}
		return null;
	}
			
}
