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

    public final int _length() {
        final int len;

        if (!(this instanceof _ArmyTextType)) {
            len = 1;
        } else if (this instanceof TextType) {
            len = 2;
        } else if (this instanceof MediumTextType) {
            len = 3;
        } else if (this instanceof _ArmyLongStringType) {
            len = 4;
        } else {
            //no bug,never here
            throw new IllegalStateException();
        }
        return len;

    }

    public static abstract class _ArmyTextType extends _SQLStringType {

    }//_ArmyTextType

    public static abstract class _ArmyLongStringType extends _ArmyTextType {


    }//_ArmyLongStringType

}
