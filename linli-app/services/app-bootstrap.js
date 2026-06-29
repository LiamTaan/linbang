import { getRoleContext, getProfile } from '@/api/member'
import { getAppSettings } from '@/api/platform'
import {
  getPlatformSettings,
  hasLogin,
  setPlatformSettings,
  setProfile,
  setRoleContext
} from '@/utils/auth'

export async function loadPlatformSettings(force = false) {
  if (!force) {
    const cache = getPlatformSettings()
    if (cache) {
      return cache
    }
  }
  const settings = await getAppSettings()
  setPlatformSettings(settings)
  return settings
}

export async function bootstrapSession() {
  const settingsPromise = loadPlatformSettings().catch(() => null)
  if (!hasLogin()) {
    return {
      settings: await settingsPromise,
      profile: null,
      roleContext: null
    }
  }
  const [profile, roleContext, settings] = await Promise.all([
    getProfile(),
    getRoleContext(),
    settingsPromise
  ])
  setProfile(profile)
  setRoleContext(roleContext)
  return {
    profile,
    roleContext,
    settings
  }
}
