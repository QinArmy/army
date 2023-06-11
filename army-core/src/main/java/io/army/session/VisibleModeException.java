package io.army.session;

/**
 * Throw when current session don't support <ul>
 * <li>{@link io.army.criteria.Visible#ONLY_NON_VISIBLE}</li>
 * <li>{@link io.army.criteria.Visible#BOTH}</li>
 * </ul>
 *
 * @see io.army.env.ArmyKey#VISIBLE_MODE
 * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
 * @since 1.0
 */
public final class VisibleModeException extends SessionException {

    public VisibleModeException(String message) {
        super(message);
    }

}
