import { DefineStoreOptions } from 'pinia'

declare module 'pinia' {
  export interface DefineStoreOptions<S, Store> {
    persist?: {
      enabled: boolean
      strategies?: {
        key: string
        storage: Storage
      }[]
    }
  }
}