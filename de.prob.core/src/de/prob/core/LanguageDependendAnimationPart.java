/**
 * 
 */
package de.prob.core;

import de.prob.exceptions.ProBException;
import de.prob.parserbase.ProBParserBase;

/**
 * 
 * 
 * @author plagge
 */
public interface LanguageDependendAnimationPart extends ProBParserBase {
	void reload(Animator animator) throws ProBException;
}
