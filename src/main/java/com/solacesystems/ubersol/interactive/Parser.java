package com.solacesystems.ubersol.interactive;

/**
 * Takes care of the heavy lifting around handling interactive commands from the user,
 * blocking until a discrete command has been parsed.
 *
 * Returns the first Cmd parsed.
 */
interface Parser {
    /**
     * Gathers input from user until a full command has been entered.
     *
     * @return Cmd object constructed from character input.
     */
    Cmd next();

    /**
     * Gathers input from user without displaying it to the console.
     *
     * @return Input string from the console.
     */
    String getPasswordSafe();
}
