package template.exception;

import static java.lang.String.format;

public final class ItemAlreadyExistsException extends RuntimeException {

    public static final String MESSAGE = "Item with id %s already exists";

    public ItemAlreadyExistsException(String id) {
        super(format(MESSAGE, id));
    }

}
