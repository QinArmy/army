package io.army.mapping;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link StringType}</li>
 *         <li>{@link TextType}</li>
 *         <li>{@link MediumTextType}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public abstract class _SQLStringType extends _ArmyInnerMapping {

    protected _SQLStringType() {

    }

    public abstract int _length();

}
