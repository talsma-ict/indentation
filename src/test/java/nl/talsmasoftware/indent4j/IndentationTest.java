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
            Indentation tabsAtLevel = Indentation.TABS.atLevel(i);
            assertThat(tabsAtLevel.getLevel()).isEqualTo(i);
            assertThat(tabsAtLevel.getUnit()).isEqualTo("\t");
            assertThat(tabsAtLevel.length()).isEqualTo(i);
            assertThat(tabsAtLevel).matches(String.format("^\\t{%d}$", i));
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

//    @Test
//    public void testSpacesWidth0() {
//        final int width = 0;
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, -1), is(sameInstance(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 0))));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 0), is(sameInstance(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.NONE)));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 1), is(sameInstance(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.NONE)));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 2), is(sameInstance(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.NONE)));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 6), is(sameInstance(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.NONE)));
//    }
//
//    @Test
//    public void testSpacesWidth1() {
//        final int width = 1;
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, -1), is(equalTo(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 0))));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 0), hasToString(""));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 1), hasToString(" "));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 2), hasToString("  "));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 6), hasToString("      "));
//    }
//
//    @Test
//    public void testSpacesWidth2() {
//        final int width = 2;
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, -1), is(sameInstance(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 0))));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 0), hasToString(""));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 1), hasToString("  "));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 2), hasToString("    "));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 6), hasToString("            "));
//    }
//
//    @Test
//    public void testSpacesWidth3() {
//        final int width = 3;
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, -1), is(equalTo(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 0))));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 0), hasToString(""));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 1), hasToString("   "));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 2), hasToString("      "));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 6), hasToString("                  "));
//    }
//
//    @Test
//    public void testSpacesWidth4() {
//        final int width = 4;
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, -1), is(sameInstance(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 0))));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 0), hasToString(""));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 1), hasToString("    "));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 2), hasToString("        "));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(width, 6), hasToString("                        "));
//    }
//
//    @Test
//    public void testDeserialization() {
//        nl.talsmasoftware.umldoclet.rendering.indent.Indentation deserialized = deserialize(serialize(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.DEFAULT));
//        assertThat(deserialized, is(sameInstance(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.DEFAULT)));
//
//        deserialized = deserialize(serialize(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(4, 3)));
//        assertThat(deserialized, is(sameInstance(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(4, 3))));
//
//        deserialized = deserialize(serialize(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.tabs(4)));
//        assertThat(deserialized, is(sameInstance(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.tabs(4))));
//
//        deserialized = deserialize(serialize(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(1, 0)));
//        assertThat(deserialized, is(equalTo(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(1, 0)))); // Not a constant; other instance
//    }
//
//    @Test
//    public void testHashcode() {
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.DEFAULT.hashCode(), is(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.DEFAULT.hashCode()));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(1, 15).hashCode(), is(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces(1, 15).hashCode()));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.tabs(28).hashCode(), is(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.tabs(28).hashCode()));
//    }
//
//    @Test
//    public void testLenght() {
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.DEFAULT.length(), is(0));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.DEFAULT.increase().length(), is(4));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.DEFAULT.increase().increase().length(), is(8));
//        assertThat(nl.talsmasoftware.umldoclet.rendering.indent.Indentation.tabs(5).length(), is(5));
//    }
//
//    @Test
//    public void testSubsequence() {
//        assertThat(Indentation.DEFAULT.increase().increase().subSequence(3, 6), hasToString("   "));
//    }
//
//    private static byte[] serialize(Serializable object) {
//        try {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
//                oos.writeObject(object);
//            }
//            return bos.toByteArray();
//        } catch (IOException ioe) {
//            throw new IllegalStateException("Couldn't serialize object: " + ioe.getMessage(), ioe);
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    private static <S extends Serializable> S deserialize(byte[] bytes) {
//        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
//            return (S) in.readObject();
//        } catch (IOException | ClassNotFoundException e) {
//            throw new IllegalStateException("Couldn't deserialize object: " + e.getMessage(), e);
//        }
//    }
}
