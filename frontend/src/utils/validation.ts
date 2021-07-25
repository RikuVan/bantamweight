export type ValidatorPredicate<T = string, K = any> = {
  (value: T, allValues?: K): boolean
}

export type Validator<T = string> = {
  (value: T, ...args: any[]): string | undefined
}

export type Formatter<T, O extends T> = {
  (value: T): O
}

export const composeValidators =
  <T extends any>(...validators: Validator<T>[]): Validator<T> =>
  (value: T, allValues: any) => {
    return validators.reduce(
      (error, validator) => error || validator(value, allValues),
      undefined as string | undefined
    )
  }

export const rule =
  <T, K = Record<string, any>>(
    isValid: ValidatorPredicate<T, K>,
    errorText: string
  ): Validator<T> =>
  (value: T, allValues: K) =>
    isValid(value, allValues) ? undefined : errorText

export const isEmail = (value = '') => /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(value)
export const isValidEmail = rule<string>(isEmail, 'Invalid email format')
export const isPassword = rule<string>(
  (value = '') => value && value?.length >= 8,
  'Min. 8 characters'
)
export const required = rule<string | number>(
  (value = '') => value !== undefined && value !== null && (value + '').length > 0,
  'Required'
)
