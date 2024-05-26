<script setup lang="ts">
import Default from '@/layouts/Default.vue'
import { http } from '@/main'
import { reactive, ref } from 'vue'
import { reactiveOmit } from '@vueuse/core'

// @ts-ignore
import VueDatePicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'

// @ts-ignore
import Multiselect from 'vue-multiselect'
import type { Challenge, ChallengeType } from '@/types'
import { useFirebaseAuth } from 'vuefire'

const auth = useFirebaseAuth()

const challenges = ref<Challenge[]>([])
const challengeTypes = ref<ChallengeType[]>([])

await Promise.all([
  http
    .get<Challenge[]>('/challenge', {
      headers: {
        Authorization: `Bearer ${await auth?.currentUser?.getIdToken()}`
      }
    })
    .then((response) => {
      challenges.value = response.data
    }),

  http
    .get<ChallengeType[]>('/challenge/types', {
      headers: {
        Authorization: `Bearer ${await auth?.currentUser?.getIdToken()}`
      }
    })
    .then((response) => {
      challengeTypes.value = response.data
    })
])

const closeDrawer = () => {
  addChallengeDialog.value = false
  challengeCreation.title = ''
  challengeCreation.description = ''
  challengeCreation.startAt = null
  challengeCreation.endAt = null
  challengeCreation.isActive = null
  challengeCreation.createdAt = null
}

const editChallenge = (challenge: Challenge) => {
  challengeCreation.uid = challenge.uid
  challengeCreation.title = challenge.title
  challengeCreation.description = challenge.description
  challengeCreation.startAt = challenge.startAt
  challengeCreation.endAt = challenge.endAt
  challengeCreation.isActive = challenge.isActive
  challengeCreation.createdAt = challenge.createdAt
  challengeCreation.challengeTypes = challenge.challengeTypes
  challengeCreation.isTrackable = challenge.isTrackable
  challengeCreation.difficulty = challenge.difficulty
  addChallengeDialog.value = true
}

const createChallenge = async () => {
  if (challengeCreation.uid) {
    await http.put(`/challenge/${challengeCreation.uid}`, challengeCreation, {
      headers: {
        Authorization: `Bearer ${await auth?.currentUser?.getIdToken()}`
      }
    })

    return
  }

  await http.post(
    '/challenge',
    reactiveOmit(challengeCreation, ['uid', 'isActive', 'createdAt', 'isTrackable', 'difficulty']),
    {
      headers: {
        Authorization: `Bearer ${await auth?.currentUser?.getIdToken()}`
      }
    }
  )
  await http
    .get<Challenge[]>('/challenge', {
      headers: {
        Authorization: `Bearer ${await auth?.currentUser?.getIdToken()}`
      }
    })
    .then((response) => {
      challenges.value = response.data
    })
  addChallengeDialog.value = false
}

const createChallengeType = async (challengeType: string) => {
  const { data: createdType } = await http.post('/challenge/types', challengeType, {
    headers: {
      Authorization: `Bearer ${await auth?.currentUser?.getIdToken()}`
    }
  })
  challengeTypes.value.push(createdType)
  challengeCreation.challengeTypes.push(createdType)
}

const addChallengeDialog = ref(false)

const addNewChallenge = () => {
  addChallengeDialog.value = true

  challengeCreation.uid = null
  challengeCreation.title = ''
  challengeCreation.description = ''
  challengeCreation.startAt = null
  challengeCreation.endAt = null
  challengeCreation.isActive = null
  challengeCreation.createdAt = null
  challengeCreation.isTrackable = null
  challengeCreation.difficulty = null
}

const challengeCreation = reactive<{
  uid: string | null
  title: string
  description: string
  challengeTypes: ChallengeType[]
  startAt: string | null
  endAt: string | null
  isActive: boolean | null
  createdAt: string | null
  isTrackable: boolean | null
  difficulty: string | null
}>({
  uid: null,
  title: '',
  description: '',
  challengeTypes: [],
  startAt: null,
  endAt: null,
  isActive: null,
  createdAt: null,
  isTrackable: null,
  difficulty: null
})

// const selectedChallengeType = ref<ChallengeType[]>([])

// watch(selectedChallengeType, (newValue) => {
//   challengeCreation.challengeTypes = newValue.map((type) => type.uid)
// })

const headers = [
  {
    title: 'Title',
    value: 'title',
    align: 'start'
  },
  {
    title: 'Challenge Type',
    value: 'challengeTypes'
  }
]
</script>

<template>
  <Default style-class="">
    <VBtn @click="addNewChallenge">Add Challenge</VBtn>
    <VDataTable
      :headers="headers"
      :items="challenges"
      :items-per-page="5"
      class="elevation-1"
      hover
      fixed-header
      show-select
      @click:row="
        (e: any, row: any) => {
          editChallenge(row.item)
        }
      "
    >
      <template #item.challengeTypes="{ item }">
        <VChip v-for="type in item['challengeTypes']" :key="type" color="primary" />
      </template>
    </VDataTable>

    <VNavigationDrawer
      :ClickOutside="closeDrawer"
      location="right"
      width="550"
      temporary
      v-model="addChallengeDialog"
    >
      <div class="flex flex-col h-full">
        <VToolbar color="primary" dark flat>
          <v-btn @click="addChallengeDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
          <v-toolbar-title>Add Challenge</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn @click="createChallenge">Save</v-btn>
        </VToolbar>
        <v-card class="h-full">
          <v-card-text>
            <v-form class="flex flex-col gap-4">
              <v-text-field
                variant="outlined"
                label="ID"
                v-model="challengeCreation.uid"
                disabled
                placeholder="Automatically generated"
                hint="This field is automatically generated"
                persistent-hint
              />
              <v-text-field variant="outlined" label="Title" v-model="challengeCreation.title" />
              <v-text-field
                variant="outlined"
                label="Description"
                v-model="challengeCreation.description"
              />
              <Multiselect
                v-model="challengeCreation.challengeTypes"
                :options="challengeTypes"
                :close-on-select="false"
                label="name"
                placeholder="Select one or more challenge types"
                multiple
                searchable
                allow-empty
                taggable
                tag-placeholder="Add this as new challenge type"
                tag-position="bottom"
                @tag="createChallengeType"
              />
              <VueDatePicker v-model="challengeCreation.startAt" utc></VueDatePicker>
              <VueDatePicker v-model="challengeCreation.endAt" utc></VueDatePicker>
              <template v-if="challengeCreation.uid != null">
                <v-checkbox v-model="challengeCreation.isActive" label="Is Active" />
                <v-checkbox v-model="challengeCreation.isTrackable" label="Is Trackable" />

                <v-radio-group v-model="challengeCreation.difficulty">
                  <v-radio label="Easy" value="EASY" />
                  <v-radio label="Medium" value="MEDIUM" />
                  <v-radio label="Hard" value="HARD" />
                </v-radio-group>
              </template>
            </v-form>
          </v-card-text>
        </v-card>
      </div>
    </VNavigationDrawer>
  </Default>
</template>

<style scoped></style>

<style src="vue-multiselect/dist/vue-multiselect.css"></style>
