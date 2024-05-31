import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';

export const useStore = create(
  immer(set => ({
    table: {
      spid: '',
      workNames: [],
      setSpid: id => set(s => { s.table.spid = id }),
      setWorkNames: wn => set(s => { s.table.workNames = wn })
    }
  }))
)