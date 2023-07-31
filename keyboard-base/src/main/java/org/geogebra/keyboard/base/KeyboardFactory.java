package org.geogebra.keyboard.base;

import java.util.Map;

import org.geogebra.keyboard.base.model.KeyboardModelFactory;

public interface KeyboardFactory {
    /**
     * Creates a math keyboard with numbers and operators.
     *
     * @return math keyboard
     */
    Keyboard createMathKeyboard();

    /**
     * Creates a math keyboard with numbers and operators and without ANS button.
     *
     * @return math keyboard without ANS
     */
    Keyboard createDefaultKeyboard();

    /**
     * Creates a function keyboard with the function buttons.
     *
     * @return function keyboard
     */
    Keyboard createFunctionsKeyboard();

    /**
     * Creates a greek keyboard with the greek letters and control buttons.
     *
     * @return greek keyboard
     */
    Keyboard createGreekKeyboard();

    /**
     * Creates a letter (or ABC) keyboard with letters on it. There is a
     * restriction on the row definitions that are passed as a String, namely he
     * bottom row has to be shorter than the top or middle row. If the
     * restrictions are not met, a {@link RuntimeException} is thrown.
     *
     * @param topRow          a list of characters that will be the buttons of the top row
     * @param middleRow       a list of characters that will the buttons of the middle row
     * @param bottomRow       a list of characters that will be the buttons of the last row
     * @param upperKeys       a map relating each character from the rows to an uppercase
     *                        character.
     * @param withGreekSwitch if switch to greek layout should be included
     * @return letter keyboard
     */
    Keyboard createLettersKeyboard(String topRow, String middleRow, String bottomRow,
                                   Map<String, String> upperKeys, boolean withGreekSwitch);

    /**
     * Calls {@link #createLettersKeyboard(String, String, String, Map, boolean)} with true to
     * include greek keyboard.
     *
     * @param topRow    a list of characters that will be the buttons of the top row
     * @param middleRow a list of characters that will the buttons of the middle row
     * @param bottomRow a list of characters that will be the buttons of the last row
     * @param upperKeys a map relating each character from the rows to an uppercase
     *                  character.
     * @return letter keyboard
     */
    Keyboard createLettersKeyboard(String topRow, String middleRow,
                                   String bottomRow, Map<String, String> upperKeys);

    /**
     * Calls {@link #createLettersKeyboard(String, String, String, Map)} with a
     * null upper keys. In this case {@link Character#toUpperCase(char)} is
     * used.
     *
     * @param topRow    a list of characters that will be the buttons of the top row
     * @param middleRow a list of characters that will the buttons of the middle row
     * @param bottomRow a list of characters that will be the buttons of the last row
     * @return letter keyboard
     */
    Keyboard createLettersKeyboard(String topRow, String middleRow,
                                   String bottomRow);

    /**
     * Creates a special symbols keyboard with symbols control buttons, and a
     * button to switch to the letters keyboard.
     *
     * @return special symbols keyboard
     */
    Keyboard createSpecialSymbolsKeyboard();
}
