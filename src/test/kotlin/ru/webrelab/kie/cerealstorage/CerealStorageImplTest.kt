package ru.webrelab.kie.cerealstorage

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CerealStorageImplTest {

    private lateinit var storage: CerealStorageImpl

    @BeforeEach
    fun setUp() {
        storage = CerealStorageImpl(10f, 25f)
    }

    @Test
    fun `CerealStorageImpl throws if containerCapacity is negative`() {
        assertThrows(IllegalArgumentException::class.java) {
            CerealStorageImpl(-4f, 10f)
        }
    }

    @Test
    fun `CerealStorageImpl throws if containerCapacity is larger than storageCapacity`() {
        assertThrows(IllegalArgumentException::class.java) {
            CerealStorageImpl(10f, 5f)
        }
    }

    @Test
    fun `addCereal adds and creates new container`() {
        assertEquals(0f, storage.addCereal(Cereal.BUCKWHEAT, 10f), 0.01f)
    }

    @Test
    fun `addCereal adds in exist container`() {
        storage.addCereal(Cereal.BUCKWHEAT, 5f)
        assertEquals(0f, storage.addCereal(Cereal.BUCKWHEAT, 5f), 0.01f)
    }

    @Test
    fun `addCereal adds and returns amount of remaining cereal`() {
        storage.addCereal(Cereal.BUCKWHEAT, 5f)
        assertEquals(1f, storage.addCereal(Cereal.BUCKWHEAT, 6f), 0.01f)
    }

    @Test
    fun `addCereal throws if amount of cereal is negative`() {
        assertThrows(IllegalArgumentException::class.java) {
            storage.addCereal(Cereal.BUCKWHEAT, -1f)
        }
    }

    @Test
    fun `addCereal throws if storageCapacity is full`() {
        storage.addCereal(Cereal.BUCKWHEAT, 10f)
        storage.addCereal(Cereal.RICE, 7f)
        assertThrows(IllegalStateException::class.java) {
            storage.addCereal(Cereal.BULGUR, 6f)
        }
    }

    @Test
    fun `getCereal takes amount of cereal out of container`() {
        storage.addCereal(Cereal.BUCKWHEAT, 10f)
        assertEquals(6f, storage.getCereal(Cereal.BUCKWHEAT, 6f), 0.01f)
    }

    @Test
    fun `getCereal takes all amount of cereal out of container`() {
        storage.addCereal(Cereal.BUCKWHEAT, 4f)
        assertEquals(4f, storage.getCereal(Cereal.BUCKWHEAT, 6f), 0.01f)
    }

    @Test
    fun `getCereal throws if amount of cereal to take out is negative`() {
        storage.addCereal(Cereal.BUCKWHEAT, 4f)
        assertThrows(IllegalArgumentException::class.java) {
            storage.getCereal(Cereal.BUCKWHEAT, -1f)
        }
    }

    @Test
    fun `removeContainer removes empty container`() {
        storage.addCereal(Cereal.BUCKWHEAT, 0f)
        assertTrue { storage.removeContainer(Cereal.BUCKWHEAT) }
    }

    @Test
    fun `removeContainer does not remove not empty container`() {
        storage.addCereal(Cereal.BUCKWHEAT, 5f)
        assertFalse { storage.removeContainer(Cereal.BUCKWHEAT) }
    }

    @Test
    fun `removeContainer does not remove not exist container`() {
        assertFalse { storage.removeContainer(Cereal.BUCKWHEAT) }
    }

    @Test
    fun `getAmount returns amount of cereal`() {
        storage.addCereal(Cereal.BUCKWHEAT, 5f)
        assertEquals(5f, storage.getAmount(Cereal.BUCKWHEAT))
    }

    @Test
    fun `getAmount returns zero amount of cereal if container doesn't exist`() {
        assertEquals(0f, storage.getAmount(Cereal.BUCKWHEAT))
    }

    @Test
    fun `getSpace returns available amount of space for cereal in the container`() {
        storage.addCereal(Cereal.BUCKWHEAT, 4f)
        assertEquals(6f, storage.getSpace(Cereal.BUCKWHEAT))
    }

    @Test
    fun `getSpace throws if container doesn't exist`() {
        assertThrows(IllegalStateException::class.java) {
            storage.getSpace(Cereal.BUCKWHEAT)
        }
    }

    @Test
    fun `toString transforms to text`() {
        storage.addCereal(Cereal.BUCKWHEAT, 4f)
        assertEquals("Крупа: Гречка, количество: 4.0", storage.toString())
    }

    @Test
    fun `toString for empty storage`() {
        assertEquals("Хранилище не содержит контейнеров", storage.toString())
    }
}
