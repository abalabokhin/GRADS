package edu.sc.csce740;

import edu.sc.csce740.model.CourseTaken;
import edu.sc.csce740.model.RequirementCheckInput;
import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.RequirementDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ProgressCheckerCommon
{

	public RequirementCheckResult CheckCoursesByInclusion(RequirementCheckInput requirementCheckInput)
	{
		Map<String,Integer> includedClassesIds = requirementCheckInput.includedCourseIds;
		List<CourseTaken> coursesTaken = requirementCheckInput.coursesTaken;
		int yearsToFinishClasses = requirementCheckInput.yearsToFinishClasses;
		int minNbrCredits = requirementCheckInput.minNbrCredits;

    	RequirementCheckResult result = new RequirementCheckResult();
    	RequirementDetails details = new RequirementDetails();

		int nbrCredits = 0;

    	for (CourseTaken courseTaken : coursesTaken)
 		{
 			// Make sure that the course is completed
 			if ((includedClassesIds.containsKey(courseTaken.course.id)) && (!courseTaken.grade.equals(CourseTaken.Grade._)))
 			{
 				int minCredits = includedClassesIds.get(courseTaken.course.id);

 				int courseCredits = Integer.parseInt(courseTaken.course.numCredits);

 				nbrCredits += courseCredits;
 				details.courses.add(courseTaken);

				// If the program course is setup with min credits then check if
				// greater then zero so we can subtract. This will allow for
				// programs that require (like 899) to have the same class taken multiple times
 				if (minCredits > 0)
 				{
					includedClassesIds.put(courseTaken.course.id,(minCredits-courseCredits));
				}

 				int updatedMinCredits = includedClassesIds.get(courseTaken.course.id);

				// If we have zero credits required remaining remove it from the required list
 				if (updatedMinCredits <= 0)
 					includedClassesIds.remove(courseTaken.course.id);
 			}
 		}

 		result.details = details;

		// If the required classes map is now empty OR we have more then required credits then
		// we met this check
 		if ((includedClassesIds.size() == 0) ||
 		   ((nbrCredits >= minNbrCredits) && (minNbrCredits > 0)))
 			result.passed = true;
 		else
 			result.passed = false;

 		return result;

	} // End of CheckCoursesByInclusion method

	public RequirementCheckResult CheckCoursesByExclusion(RequirementCheckInput requirementCheckInput)
	{
		Map<String,Integer> excludedClassesIds = requirementCheckInput.excludedCourseIds;
		List<CourseTaken> coursesTaken = requirementCheckInput.coursesTaken;
		int yearsToFinishClasses = requirementCheckInput.yearsToFinishClasses;
		int minNbrCredits = requirementCheckInput.minNbrCredits;
		boolean graduateLevel = requirementCheckInput.graduateLevel;
		boolean csce700Level = requirementCheckInput.csce700Level;

      	RequirementCheckResult result = new RequirementCheckResult();
        RequirementDetails details = new RequirementDetails();

		int nbrCredits = 0;

        for (CourseTaken courseTaken : coursesTaken)
 		{
 			String courseId = courseTaken.course.id;

			// Make sure we have a valid course id and that the course is completed
 			if ((courseId.length() == 7) && (!courseTaken.grade.equals(CourseTaken.Grade._)))
 			{
				String courseDepart = courseId.substring(0,4);
				int courseNumber = Integer.parseInt(courseId.substring(4));

				Object excludedClass = excludedClassesIds.get(courseId);

				// Check to see if the class taken is listed in the excluded class map
				if (excludedClass == null)
				{
					// Class is not in the excluded map
					// Now check to see if
					// 1) Requirment is graduate so course id > 500
					// 2) Requirement is CSCE >= 700 so check the department code and course id
					if (((graduateLevel) && (courseNumber >= 500)) ||
 				       ((csce700Level) && (courseNumber >= 700) && (courseDepart.equals("csce"))))
 					{
						details.courses.add(courseTaken);
 						nbrCredits += Integer.parseInt(courseTaken.course.numCredits);
					}
				}
 				// Course IS listed as excluded
 				// Check to see if the max number is greater then zero as we might be allowed to take the class once
 				else
 				{
					int maxCredits = excludedClassesIds.get(courseId);

					if (((maxCredits > 0) && (graduateLevel) && (courseNumber >= 500)) ||
					   ((maxCredits > 0) && (csce700Level) && (courseNumber >= 700) && (courseDepart.equals("csce"))))
					{
						int tempNbrCredits = Integer.parseInt(courseTaken.course.numCredits);

						if (tempNbrCredits <= maxCredits)
						{
							nbrCredits += Integer.parseInt(courseTaken.course.numCredits);
							excludedClassesIds.put(courseId, maxCredits - tempNbrCredits);
						}
						else
						{
							excludedClassesIds.put(courseId, 0);
							nbrCredits += maxCredits;
						}
					}
 				} // End of if for excluded class == null (found or not found)
			}// End of if for valid course id and completed course
 		} // End of for loop for completed courses

 		result.details = details;

		if (nbrCredits >= minNbrCredits)
			result.passed = true;
 		else
 			result.passed = false;

 		return result;

	} // End of CheckCoursesByExclusion method

} // End of ProgressCheckerCommon class
