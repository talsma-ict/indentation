/*
 * Copyright 2025 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.talsmasoftware.indent4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Sjoerd Talsma
 */
public class IndentationTest {
    @Test
    @DisplayName("Indentation.EMPTY: Test indentation levels")
    public void testEmpty() {
        // Level 0 assertions
        assertThat(Indentation.EMPTY).hasToString("");
        assertThat(Indentation.EMPTY.getLevel()).isZero();
        assertThat(Indentation.EMPTY.getUnit()).isEmpty();
        assertThat(Indentation.EMPTY.unindent()).isSameAs(Indentation.EMPTY);

        // Level 1 assertions
        Indentation emptyLevel1 = Indentation.EMPTY.indent();
        assertThat(emptyLevel1).hasToString("");
        assertThat(emptyLevel1.getLevel()).isEqualTo(1);
        assertThat(emptyLevel1.getUnit()).isEmpty();

        assertThat(emptyLevel1.unindent()).isSameAs(Indentation.EMPTY);
        assertThat(emptyLevel1.indent().unindent()).isSameAs(emptyLevel1);
        assertThat(emptyLevel1.unindent().indent()).isSameAs(emptyLevel1);

        // NONE.atLevel should maintain indentation levels
        for (int i = 2; i < 256; i++) {
            Indentation emptyAtLevel = Indentation.EMPTY.atLevel(i);
            assertThat(emptyAtLevel.getLevel()).isEqualTo(i);
            assertThat(emptyAtLevel.getUnit()).isEmpty();
            assertThat(emptyAtLevel.length()).isZero();
            assertThat(emptyAtLevel).hasToString("");
        }
    }

    @Test
    @DisplayName("Indentation.TABS: Test indentation levels")
    public void testTabs() {
        // Level 0 assertions
        assertThat(Indentation.TABS).hasToString("");
        assertThat(Indentation.TABS.getLevel()).isZero();
        assertThat(Indentation.TABS.getUnit()).isEqualTo("\t");
        assertThat(Indentation.TABS.unindent()).isSameAs(Indentation.TABS);

        // Level 1 assertions
        Indentation tabLevel1 = Indentation.TABS.indent();
        assertThat(tabLevel1).hasToString("\t");
        assertThat(tabLevel1.getLevel()).isEqualTo(1);
        assertThat(tabLevel1.getUnit()).isEqualTo("\t");

        assertThat(tabLevel1.unindent()).isSameAs(Indentation.TABS);
        assertThat(tabLevel1.indent().unindent()).isSameAs(tabLevel1);
        assertThat(tabLevel1.unindent().indent()).isSameAs(tabLevel1);

        // NONE.atLevel should maintain indentation levels
        for (int i = 2; i < 256; i++) {
            Indentation atLevel = Indentation.TABS.atLevel(i);
            assertThat(atLevel.getLevel()).isEqualTo(i);
            assertThat(atLevel.getUnit()).isEqualTo("\t");
            assertThat(atLevel.length()).isEqualTo(i);
            assertThat(atLevel).matches(String.format("^\\t{%d}$", i));
        }
    }

    @Test
    @DisplayName("Indentation.of(single space): Test indentation levels")
    public void testSingleSpaces() {
        final Indentation singleSpaces = Indentation.of(" ");

        // Level 0 assertions
        assertThat(singleSpaces).hasToString("");
        assertThat(singleSpaces.getLevel()).isZero();
        assertThat(singleSpaces.getUnit()).isEqualTo(" ");
        assertThat(singleSpaces.unindent()).isSameAs(singleSpaces);

        // Level 1 assertions
        Indentation level1 = singleSpaces.indent();
        assertThat(level1).hasToString(" ");
        assertThat(level1.getLevel()).isEqualTo(1);
        assertThat(level1.getUnit()).isEqualTo(" ");

        assertThat(level1.unindent()).isSameAs(singleSpaces);
        assertThat(level1.indent().unindent()).isSameAs(level1);
        assertThat(level1.unindent().indent()).isSameAs(level1);

        // NONE.atLevel should maintain indentation levels
        for (int i = 2; i < 256; i++) {
            Indentation atLevel = singleSpaces.atLevel(i);
            assertThat(atLevel.getLevel()).isEqualTo(i);
            assertThat(atLevel.getUnit()).isEqualTo(" ");
            assertThat(atLevel.length()).isEqualTo(i);
            assertThat(atLevel).matches(String.format("^\\s{%d}$", i));
        }
    }

    @Test
    @DisplayName("Indentation.TWO_SPACES: Test indentation levels")
    public void testTwoSpaces() {
        // Level 0 assertions
        assertThat(Indentation.TWO_SPACES).hasToString("");
        assertThat(Indentation.TWO_SPACES.getLevel()).isZero();
        assertThat(Indentation.TWO_SPACES.getUnit()).isEqualTo("  ");
        assertThat(Indentation.TWO_SPACES.unindent()).isSameAs(Indentation.TWO_SPACES);

        // Level 1 assertions
        Indentation level1 = Indentation.TWO_SPACES.indent();
        assertThat(level1).hasToString("  ");
        assertThat(level1.getLevel()).isEqualTo(1);
        assertThat(level1.getUnit()).isEqualTo("  ");

        assertThat(level1.unindent()).isSameAs(Indentation.TWO_SPACES);
        assertThat(level1.indent().unindent()).isSameAs(level1);
        assertThat(level1.unindent().indent()).isSameAs(level1);

        // NONE.atLevel should maintain indentation levels
        for (int i = 2; i < 256; i++) {
            Indentation atLevel = Indentation.TWO_SPACES.atLevel(i);
            assertThat(atLevel.getLevel()).isEqualTo(i);
            assertThat(atLevel.getUnit()).isEqualTo("  ");
            assertThat(atLevel.length()).isEqualTo(2 * i);
            assertThat(atLevel).matches(String.format("^(\\s){%d}$", 2 * i));
        }
    }

    @Test
    @DisplayName("Indentation.of(three spaces): Test indentation levels")
    public void testThreeSpaces() {
        final Indentation threeSpaces = Indentation.of("   ");

        // Level 0 assertions
        assertThat(threeSpaces).hasToString("");
        assertThat(threeSpaces.getLevel()).isZero();
        assertThat(threeSpaces.getUnit()).isEqualTo("   ");
        assertThat(threeSpaces.unindent()).isSameAs(threeSpaces);

        // Level 1 assertions
        Indentation level1 = threeSpaces.indent();
        assertThat(level1).hasToString("   ");
        assertThat(level1.getLevel()).isEqualTo(1);
        assertThat(level1.getUnit()).isEqualTo("   ");

        assertThat(level1.unindent()).isSameAs(threeSpaces);
        assertThat(level1.indent().unindent()).isSameAs(level1);
        assertThat(level1.unindent().indent()).isSameAs(level1);

        // NONE.atLevel should maintain indentation levels
        for (int i = 2; i < 256; i++) {
            Indentation atLevel = threeSpaces.atLevel(i);
            assertThat(atLevel.getLevel()).isEqualTo(i);
            assertThat(atLevel.getUnit()).isEqualTo("   ");
            assertThat(atLevel.length()).isEqualTo(3 * i);
            assertThat(atLevel).matches(String.format("^(\\s){%d}$", 3 * i));
        }
    }

    @Test
    @DisplayName("Indentation.FOUR_SPACES: Test indentation levels")
    public void testFourSpaces() {
        // Level 0 assertions
        assertThat(Indentation.FOUR_SPACES).hasToString("");
        assertThat(Indentation.FOUR_SPACES.getLevel()).isZero();
        assertThat(Indentation.FOUR_SPACES.getUnit()).isEqualTo("    ");
        assertThat(Indentation.FOUR_SPACES.unindent()).isSameAs(Indentation.FOUR_SPACES);

        // Level 1 assertions
        Indentation level1 = Indentation.FOUR_SPACES.indent();
        assertThat(level1).hasToString("    ");
        assertThat(level1.getLevel()).isEqualTo(1);
        assertThat(level1.getUnit()).isEqualTo("    ");

        assertThat(level1.unindent()).isSameAs(Indentation.FOUR_SPACES);
        assertThat(level1.indent().unindent()).isSameAs(level1);
        assertThat(level1.unindent().indent()).isSameAs(level1);

        // NONE.atLevel should maintain indentation levels
        for (int i = 2; i < 256; i++) {
            Indentation atLevel = Indentation.FOUR_SPACES.atLevel(i);
            assertThat(atLevel.getLevel()).isEqualTo(i);
            assertThat(atLevel.getUnit()).isEqualTo("    ");
            assertThat(atLevel.length()).isEqualTo(4 * i);
            assertThat(atLevel).matches(String.format("^(\\s){%d}$", 4 * i));
        }
    }

    @Test
    @DisplayName("Indentation.of(null): Must throw a NullPointerException with a sensible message.")
    void testOfNull() {
        assertThatThrownBy(() -> Indentation.of(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Indentation unit cannot be null.");
    }

    @Test
    @DisplayName("Indentation.of(empty): Check that the EMPTY constant is returned.")
    void testOfEmpty() {
        assertThat(Indentation.of("")).isSameAs(Indentation.EMPTY);
    }

    @Test
    @DisplayName("Indentation.of(tab): Check that the TABS constant is returned.")
    void testOfTabs() {
        assertThat(Indentation.of("\t")).isSameAs(Indentation.TABS);
    }

    @Test
    @DisplayName("Indentation.of(two spaces): Check that the TWO_SPACES constant is returned.")
    void testOfTwoSpaces() {
        assertThat(Indentation.of("  ")).isSameAs(Indentation.TWO_SPACES);
    }

    @Test
    @DisplayName("Indentation.of(four spaces): Check that the FOUR_SPACES constant is returned.")
    void testOfFourSpaces() {
        assertThat(Indentation.of("    ")).isSameAs(Indentation.FOUR_SPACES);
    }

    @Test
    @DisplayName("Indentation.of(\"+:\"): Check that the pattern is repeated correctly")
    void testOfPlusColon() {
        final Indentation subject = Indentation.of("+:");

        // level 0 assertions
        assertThat(subject).isEmpty();
        assertThat(subject.getLevel()).isZero();
        assertThat(subject.getUnit()).isEqualTo("+:");
        assertThat(subject.unindent()).isSameAs(subject);

        // level 1 assertions
        final Indentation level1 = subject.indent();
        assertThat(level1).hasToString("+:");
        assertThat(level1.getLevel()).isEqualTo(1);
        assertThat(level1.getUnit()).isEqualTo("+:");
        assertThat(level1.unindent()).isSameAs(subject);

        // level 2 assertions
        final Indentation level2 = level1.indent();
        assertThat(level2).hasToString("+:+:");
        assertThat(level2.getLevel()).isEqualTo(2);
        assertThat(level2.getUnit()).isEqualTo("+:");
        assertThat(level2.unindent()).isSameAs(level1);

        // higher level assertions
        for (int i = 3; i < 256; i++) {
            final Indentation atLevel = subject.atLevel(i);
            assertThat(atLevel.getLevel()).isEqualTo(i);
            assertThat(atLevel.getUnit()).isEqualTo("+:");
            assertThat(atLevel.length()).isEqualTo(2 * i);
            assertThat(atLevel).matches(String.format("^(\\+:){%d}$", i));
        }
    }

    @Test
    @DisplayName("Indentation.of(\" :\"): Check that the pattern is repeated correctly")
    void testOfSpaceColon() {
        final Indentation subject = Indentation.of(" :");

        // level 0 assertions
        assertThat(subject).isEmpty();
        assertThat(subject.getLevel()).isZero();
        assertThat(subject.getUnit()).isEqualTo(" :");
        assertThat(subject.unindent()).isSameAs(subject);

        // level 1 assertions
        final Indentation level1 = subject.indent();
        assertThat(level1).hasToString(" :");
        assertThat(level1.getLevel()).isEqualTo(1);
        assertThat(level1.getUnit()).isEqualTo(" :");
        assertThat(level1.unindent()).isSameAs(subject);

        // level 2 assertions
        final Indentation level2 = level1.indent();
        assertThat(level2).hasToString(" : :");
        assertThat(level2.getLevel()).isEqualTo(2);
        assertThat(level2.getUnit()).isEqualTo(" :");
        assertThat(level2.unindent()).isSameAs(level1);

        // higher level assertions
        for (int i = 3; i < 256; i++) {
            final Indentation atLevel = subject.atLevel(i);
            assertThat(atLevel.getLevel()).isEqualTo(i);
            assertThat(atLevel.getUnit()).isEqualTo(" :");
            assertThat(atLevel.length()).isEqualTo(2 * i);
            assertThat(atLevel).matches(String.format("^( :){%d}$", i));
        }
    }

    @Test
    @DisplayName("Indentation.of(\"   :\"): Check that the pattern is repeated correctly")
    void testOfThreeSpaceColon() {
        final Indentation subject = Indentation.of("   :");

        // level 0 assertions
        assertThat(subject).isEmpty();
        assertThat(subject.getLevel()).isZero();
        assertThat(subject.getUnit()).isEqualTo("   :");
        assertThat(subject.unindent()).isSameAs(subject);

        // level 1 assertions
        final Indentation level1 = subject.indent();
        assertThat(level1).hasToString("   :");
        assertThat(level1.getLevel()).isEqualTo(1);
        assertThat(level1.getUnit()).isEqualTo("   :");
        assertThat(level1.unindent()).isSameAs(subject);

        // level 2 assertions
        final Indentation level2 = level1.indent();
        assertThat(level2).hasToString("   :   :");
        assertThat(level2.getLevel()).isEqualTo(2);
        assertThat(level2.getUnit()).isEqualTo("   :");
        assertThat(level2.unindent()).isSameAs(level1);

        // higher level assertions
        for (int i = 3; i < 256; i++) {
            final Indentation atLevel = subject.atLevel(i);
            assertThat(atLevel.getLevel()).isEqualTo(i);
            assertThat(atLevel.getUnit()).isEqualTo("   :");
            assertThat(atLevel.length()).isEqualTo(4 * i);
            assertThat(atLevel).matches(String.format("^(   :){%d}$", i));
        }
    }

    @Test
    @DisplayName("Indentation.of(\"  ->\"): Check that the pattern is repeated correctly")
    void testOfArrowPos3() {
        final Indentation subject = Indentation.of("  ->");

        // level 0 assertions
        assertThat(subject).isEmpty();
        assertThat(subject.getLevel()).isZero();
        assertThat(subject.getUnit()).isEqualTo("  ->");
        assertThat(subject.unindent()).isSameAs(subject);

        // level 1 assertions
        final Indentation level1 = subject.indent();
        assertThat(level1).hasToString("  ->");
        assertThat(level1.getLevel()).isEqualTo(1);
        assertThat(level1.getUnit()).isEqualTo("  ->");
        assertThat(level1.unindent()).isSameAs(subject);

        // level 2 assertions
        final Indentation level2 = level1.indent();
        assertThat(level2).hasToString("  ->  ->");
        assertThat(level2.getLevel()).isEqualTo(2);
        assertThat(level2.getUnit()).isEqualTo("  ->");
        assertThat(level2.unindent()).isSameAs(level1);

        // higher level assertions
        for (int i = 3; i < 256; i++) {
            final Indentation atLevel = subject.atLevel(i);
            assertThat(atLevel.getLevel()).isEqualTo(i);
            assertThat(atLevel.getUnit()).isEqualTo("  ->");
            assertThat(atLevel.length()).isEqualTo(4 * i);
            assertThat(atLevel).matches(String.format("^(  ->){%d}$", i));
        }
    }

    @Test
    @DisplayName("Indentation.of(\" -> \"): Check that the pattern is repeated correctly")
    void testOfArrowPos2() {
        final Indentation subject = Indentation.of(" -> ");

        // level 0 assertions
        assertThat(subject).isEmpty();
        assertThat(subject.getLevel()).isZero();
        assertThat(subject.getUnit()).isEqualTo(" -> ");
        assertThat(subject.unindent()).isSameAs(subject);

        // level 1 assertions
        final Indentation level1 = subject.indent();
        assertThat(level1).hasToString(" -> ");
        assertThat(level1.getLevel()).isEqualTo(1);
        assertThat(level1.getUnit()).isEqualTo(" -> ");
        assertThat(level1.unindent()).isSameAs(subject);

        // level 2 assertions
        final Indentation level2 = level1.indent();
        assertThat(level2).hasToString(" ->  -> ");
        assertThat(level2.getLevel()).isEqualTo(2);
        assertThat(level2.getUnit()).isEqualTo(" -> ");
        assertThat(level2.unindent()).isSameAs(level1);

        // higher level assertions
        for (int i = 3; i < 256; i++) {
            final Indentation atLevel = subject.atLevel(i);
            assertThat(atLevel.getLevel()).isEqualTo(i);
            assertThat(atLevel.getUnit()).isEqualTo(" -> ");
            assertThat(atLevel.length()).isEqualTo(4 * i);
            assertThat(atLevel).matches(String.format("^( -> ){%d}$", i));
        }
    }

    @Test
    @DisplayName("Indentation.of(\"==> \"): Check that the pattern is repeated correctly")
    void testOfFatArrow() {
        final Indentation subject = Indentation.of("==> ");

        // level 0 assertions
        assertThat(subject).isEmpty();
        assertThat(subject.getLevel()).isZero();
        assertThat(subject.getUnit()).isEqualTo("==> ");
        assertThat(subject.unindent()).isSameAs(subject);

        // level 1 assertions
        final Indentation level1 = subject.indent();
        assertThat(level1).hasToString("==> ");
        assertThat(level1.getLevel()).isEqualTo(1);
        assertThat(level1.getUnit()).isEqualTo("==> ");
        assertThat(level1.unindent()).isSameAs(subject);

        // level 2 assertions
        final Indentation level2 = level1.indent();
        assertThat(level2).hasToString("==> ==> ");
        assertThat(level2.getLevel()).isEqualTo(2);
        assertThat(level2.getUnit()).isEqualTo("==> ");
        assertThat(level2.unindent()).isSameAs(level1);

        // higher level assertions
        for (int i = 3; i < 256; i++) {
            final Indentation atLevel = subject.atLevel(i);
            assertThat(atLevel.getLevel()).isEqualTo(i);
            assertThat(atLevel.getUnit()).isEqualTo("==> ");
            assertThat(atLevel.length()).isEqualTo(4 * i);
            assertThat(atLevel).matches(String.format("^(==> ){%d}$", i));
        }
    }

    @Test
    @DisplayName("Indentation.of(any Indentation): Should return itself at level 0.")
    void testOfAnyIndentationReturnsItselfAtLevel0() {
        assertThat(Indentation.of(Indentation.EMPTY)).isSameAs(Indentation.EMPTY);
        assertThat(Indentation.of(Indentation.EMPTY.indent())).isSameAs(Indentation.EMPTY);
        assertThat(Indentation.of(Indentation.TABS)).isSameAs(Indentation.TABS);
        assertThat(Indentation.of(Indentation.TABS.indent())).isSameAs(Indentation.TABS);
        assertThat(Indentation.of(Indentation.TWO_SPACES)).isSameAs(Indentation.TWO_SPACES);
        assertThat(Indentation.of(Indentation.TWO_SPACES.indent())).isSameAs(Indentation.TWO_SPACES);
        assertThat(Indentation.of(Indentation.FOUR_SPACES)).isSameAs(Indentation.FOUR_SPACES);
        assertThat(Indentation.of(Indentation.FOUR_SPACES.indent())).isSameAs(Indentation.FOUR_SPACES);

        Indentation arrow = Indentation.of(" -> ");
        assertThat(Indentation.of(arrow)).isSameAs(arrow);
        assertThat(Indentation.of(arrow.indent())).isSameAs(arrow);
    }

    @Test
    @DisplayName("Indentation.atLevel: Must throw IllegalArgumentException with a sensible message.")
    void atLevelNegativeNotAllowed() {
        assertThatThrownBy(() -> Indentation.EMPTY.atLevel(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Indentation level may not be negative: -1");
    }

    @Test
    @DisplayName("Indentation.length: Must be the indentation level times the unit length.")
    void lengthMustBeCorrect() {
        for (int i = 0; i < 512; i++) {
            assertThat(Indentation.EMPTY.atLevel(i).length()).isZero();
            assertThat(Indentation.TABS.atLevel(i).length()).isEqualTo(i);
            assertThat(Indentation.TWO_SPACES.atLevel(i).length()).isEqualTo(2 * i);
            assertThat(Indentation.FOUR_SPACES.atLevel(i).length()).isEqualTo(4 * i);
        }
    }

    @Test
    @DisplayName("Indentation.charAt: Must throw IndexOutOfBoundsException when index is out of bounds.")
    void charAtBoundsCheck() {
        assertThatThrownBy(() -> Indentation.EMPTY.charAt(-1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> Indentation.EMPTY.charAt(0)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("Indentation.charAt: Must return the correct characters.")
    void charAt() {
        Indentation subject = Indentation.of("01234").atLevel(2);
        assertThat(subject.charAt(0)).isEqualTo('0');
        assertThat(subject.charAt(1)).isEqualTo('1');
        assertThat(subject.charAt(2)).isEqualTo('2');
        assertThat(subject.charAt(3)).isEqualTo('3');
        assertThat(subject.charAt(4)).isEqualTo('4');
        assertThat(subject.charAt(5)).isEqualTo('0');
        assertThat(subject.charAt(6)).isEqualTo('1');
        assertThat(subject.charAt(7)).isEqualTo('2');
        assertThat(subject.charAt(8)).isEqualTo('3');
        assertThat(subject.charAt(9)).isEqualTo('4');
    }

    @Test
    @DisplayName("Indentation.subSequence: Must throw IndexOutOfBoundsException when index is out of bounds.")
    void subSequenceBoundsCheck() {
        assertThat(Indentation.EMPTY.subSequence(0, 0)).isEmpty();
        assertThatThrownBy(() -> Indentation.EMPTY.subSequence(-1, 0)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> Indentation.EMPTY.subSequence(1, 0)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("Indentation.subSequence: Must return the correct subsequence.")
    void subSequence() {
        Indentation subject = Indentation.of("01234").atLevel(2);
        assertThat(subject.subSequence(0, 2)).isEqualTo("01");
        assertThat(subject.subSequence(2, 4)).isEqualTo("23");
        assertThat(subject.subSequence(4, 6)).isEqualTo("40");
    }

    @Test
    @DisplayName("Indentation.hashCode: Must produce consistent results.")
    void testHashCode() {
        Indentation subject = Indentation.of("01234").atLevel(2);
        assertThat(subject.hashCode()).hasSameHashCodeAs(subject);
        assertThat(subject.hashCode()).hasSameHashCodeAs(Indentation.of("01234").atLevel(2));
        assertThat(subject.hashCode()).hasSameHashCodeAs("0123401234");
    }

    @Test
    @DisplayName("Indentation.equals: Other object must be an indentation with equal level and unit.")
    void testEquals() {
        Indentation subject = Indentation.of("01234").atLevel(2);
        assertThat(subject.equals(null)).isFalse();
        assertThat(subject.equals(subject.atLevel(0))).isFalse();
        assertThat(subject.equals(subject.atLevel(1))).isFalse();
        assertThat(subject.equals(subject.atLevel(2))).isTrue();
        assertThat(subject.equals(subject.atLevel(3))).isFalse();
        assertThat(subject.equals("0123401234")).isFalse();
        assertThat(subject.equals(subject.indent().unindent())).isTrue();
        // Check uncached indentation equality too.
        assertThat(subject.equals(Indentation.of("01234").atLevel(2))).isTrue();
        assertThat(subject.atLevel(10000).equals(Indentation.of("01234").atLevel(10000))).isTrue();
    }

    @Test
    @DisplayName("Indentation serialization: Serializing + deserializing must return equal Indentation.")
    void serializationMustBeReversible() {
        Indentation subject = Indentation.of("01234").atLevel(2);
        byte[] serialized = serialize(subject);
        Indentation deserialized = deserialize(serialized);
        assertThat(deserialized).isEqualTo(subject);
    }

    @Test
    @DisplayName("Indentation serialization: Empty indentation returns same constant upon deserialization.")
    void deserializationOfEmpty() {
        Indentation deserialized = deserialize(serialize(Indentation.EMPTY));
        assertThat(deserialized).isSameAs(Indentation.EMPTY);
    }

    @Test
    @DisplayName("Indentation serialization: Indentation.TABS indentation returns cached constant upon deserialization.")
    void deserializationOfTabs() {
        Indentation deserialized = deserialize(serialize(Indentation.TABS.atLevel(4)));
        assertThat(deserialized).isSameAs(Indentation.TABS.atLevel(4));
    }

    @Test
    @DisplayName("Indentation serialization: Two spaces indentation returns cached constant upon deserialization.")
    void deserializationOfTwoSpaces() {
        Indentation deserialized = deserialize(serialize(Indentation.of("  ").atLevel(10)));
        assertThat(deserialized).isSameAs(Indentation.TWO_SPACES.atLevel(10));
    }

    @Test
    @DisplayName("Indentation serialization: Four spaces indentation returns cached constant upon deserialization.")
    void deserializationOfFourSpaces() {
        Indentation deserialized = deserialize(serialize(Indentation.FOUR_SPACES.atLevel(16)));
        assertThat(deserialized).isSameAs(Indentation.FOUR_SPACES.atLevel(16));
    }

    static byte[] serialize(Serializable object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            oos.flush();
            return baos.toByteArray();
        } catch (IOException | RuntimeException e) {
            throw new AssertionError("Serialization failure: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends Serializable> T deserialize(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new AssertionError("Deserialization failure: " + e.getMessage(), e);
        }
    }
}
