export interface User {}

export interface ChallengeType {
  uid: string
  name: string
}

export interface Challenge {
  uid: string
  title: string
  description: string
  createdAt: string
  suggestedBy: User
  challengeTypes: ChallengeType[]
  isActive: boolean
  startAt: string
  endAt: string
  isTrackable: boolean
  difficulty: string
}
