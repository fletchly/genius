package org.fletchly.genius;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

public class GeniusTests
{
    private ServerMock serverMock;
    private Genius genius;

    @BeforeEach
    void setUp()
    {
        serverMock = MockBukkit.mock();
        genius = MockBukkit.load(Genius.class);
    }

    @AfterEach
    void tearDown()
    {
        MockBukkit.unmock();
    }
}
